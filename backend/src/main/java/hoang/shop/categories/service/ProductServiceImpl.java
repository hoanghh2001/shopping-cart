package hoang.shop.categories.service;

import com.github.slugify.Slugify;
import hoang.shop.categories.dto.request.*;
import hoang.shop.categories.dto.response.AdminListItemProductResponse;
import hoang.shop.categories.dto.response.PriceTuple;
import hoang.shop.categories.dto.response.ProductDetailResponse;
import hoang.shop.categories.dto.response.ProductListItemResponse;
import hoang.shop.categories.model.*;
import hoang.shop.categories.repository.*;
import hoang.shop.categories.spec.ProductSpec;
import hoang.shop.common.IdListRequest;
import hoang.shop.common.enums.ColorFamily;
import org.springframework.cache.annotation.Cacheable;
import lombok.RequiredArgsConstructor;
import hoang.shop.categories.mapper.ProductMapper;
import hoang.shop.common.enums.status.ProductStatus;
import hoang.shop.common.exception.BadRequestException;
import hoang.shop.common.exception.DuplicateResourceException;
import hoang.shop.common.exception.NotFoundException;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;
    private final ProductMapper productMapper;
    private final ProductReviewStatsRepository statsRepository;
    private final BrandRepository brandRepository;
    private final CategoryRepository categoryRepository;
    private final ProductTagRepository productTagRepository;
    private final ProductColorRepository colorRepository;
    private final ProductColorImageRepository imageRepository;
    private final ProductVariantRepository variantRepository;

    @Override
    public AdminListItemProductResponse create(ProductCreateRequest request) {


        if (productRepository.existsByName(request.name()))
            throw new DuplicateResourceException("{error.product.name.duplicate}");
        String name = request.name().trim().toLowerCase(Locale.ROOT);
        String slug = Slugify.builder().build().slugify(request.name());
        if (productRepository.existsBySlug(slug))
            throw new DuplicateResourceException("{error.product.slug.duplicate}");
        Product product = productMapper.toEntity(request);
        if (request.brandId() != null) {
            Brand brand = brandRepository.findById(request.brandId())
                    .orElseThrow(() -> new NotFoundException("{error.brand.id.not-found}"));
            product.setBrand(brand);
        }
        if (request.categoryId() != null) {
            Category category = categoryRepository.findById(request.categoryId())
                    .orElseThrow(() -> new NotFoundException("{error.category.id.not-found}"));
            product.setCategory(category);
        }
        product.setSlug(slug);
        Product saved = productRepository.save(product);
        ProductReviewStats stats = ProductReviewStats.builder().productId(saved.getId()).build();
        statsRepository.save(stats);
        return productMapper.toAdminListItemResponse(saved);
    }

    @Override
    public AdminListItemProductResponse update(Long productId, ProductUpdateRequest request) {
        if (productRepository.existsByNameAndIdNot(request.name(), productId))
            throw new DuplicateResourceException("{error.product.name.duplicate}");
        if (productRepository.existsBySlugAndIdNot(request.slug(), productId))
            throw new DuplicateResourceException("{error.product.slug.duplicate}");
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new NotFoundException("{error.product.id.not-found}"));
        productMapper.merge(request, product);
        return productMapper.toAdminListItemResponse(product);
    }

    @Override
    public AdminListItemProductResponse getById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("{error.product.id.not-found}"));
        return productMapper.toAdminListItemResponse(product);
    }

    @Override
    public AdminListItemProductResponse getByName(String name) {
        Product product = productRepository.findByName(name)
                .orElseThrow(() -> new NotFoundException("{error.product.name.not-found}"));
        return productMapper.toAdminListItemResponse(product);
    }

    @Override
    public Page<AdminListItemProductResponse> searchForAdmin(ProductSearchCondition condition, Pageable pageable) {
        Specification<Product> spec = ProductSpec.build(condition);
        Page<Product> page = productRepository.findAll(spec, pageable);
        return page.map(productMapper::toAdminListItemResponse);
    }

    @Override
    public int deleteById(IdListRequest ids) {
        List<Product> productList = productRepository.findAllById(ids.ids());
        if (productList.size() != ids.ids().size())
            throw new NotFoundException("{error.product.id.not-found}");
        boolean isInvalid = productList.stream()
                .anyMatch(p -> p.getStatus() != ProductStatus.ACTIVE);
        if (isInvalid)
            throw new BadRequestException("error.product.id.badRequest");
        return productRepository.updateStatusById(ids.ids(), ProductStatus.DELETED);
    }

    @Override
    public int restoreById(IdListRequest ids) {
        List<Product> productList = productRepository.findAllById(ids.ids());
        if (productList.size() != ids.ids().size())
            throw new NotFoundException("{error.product.id.not-found}");
        boolean isInvalid = productList.stream()
                .anyMatch(p -> p.getStatus() != ProductStatus.DELETED);
        if (isInvalid)
            throw new BadRequestException("error.product.id.badRequest");
        return productRepository.updateStatusById(ids.ids(), ProductStatus.ACTIVE);
    }

    @Override
    public ProductDetailResponse getActiveBySlug(String slug) {
        Product product = productRepository.findBySlugAndStatus(slug, ProductStatus.ACTIVE)
                .orElseThrow(() -> new NotFoundException("{error.product.slug.not-found}"));

        ProductReviewStats stats = statsRepository.findByProductId(product.getId())
                .orElse(null);

        return productMapper.toDetailResponse(product, stats);
    }

    @Override
    @Transactional(readOnly = true)
    public Slice<ProductListItemResponse> search(PublicProductSearchCondition condition, Pageable pageable) {

        Specification<Product> spec = ProductSpec.buildPublic(condition);

        ColorFamily filterColor = null;
        if (condition != null && condition.colors() != null && !condition.colors().isEmpty()) {
            try {
                filterColor = ColorFamily.valueOf(condition.colors().getFirst().toUpperCase());
            } catch (IllegalArgumentException ex) {
                filterColor = null;
            }
        }

        // 2) Page products
        Slice<Product> page = productRepository.findAll(spec, pageable);
        List<Long> productIds = page.getContent().stream()
                .map(Product::getId)
                .toList();

        if (productIds.isEmpty()) {
            return Page.empty(pageable);
        }

        // 3) Bulk maps/sets
        Map<Long, ProductReviewStats> statsMap =
                statsRepository.findByProductIdIn(productIds).stream()
                        .collect(Collectors.toMap(
                                ProductReviewStats::getProductId,
                                Function.identity(),
                                (a, b) -> a
                        ));

        Map<Long, ProductColor> mainColorMap =
                colorRepository.findMainColorsByProductIds(productIds).stream()
                        .collect(Collectors.toMap(
                                pc -> pc.getProduct().getId(),
                                Function.identity(),
                                (a, b) -> a
                        ));

        Set<Long> inStockProductIds =
                new HashSet<>(variantRepository.findInStockProductIds(productIds));

        // 4) Build imageMap/priceMap bằng biến tạm rồi "chốt final"
        Map<Long, ProductColorImage> tmpImageMap = Map.of();
        Map<Long, PriceTuple> tmpPriceMap = Map.of();

        if (filterColor != null) {
            tmpImageMap = imageRepository
                    .findMainImagesByProductIdsAndColorFamily(productIds, filterColor)
                    .stream()
                    .collect(Collectors.toMap(
                            img -> img.getColor().getProduct().getId(),
                            Function.identity(),
                            (a, b) -> a
                    ));

            List<Long> colorIds = tmpImageMap.values().stream()
                    .map(img -> img.getColor().getId())
                    .distinct()
                    .toList();

            if (!colorIds.isEmpty()) {
                tmpPriceMap = variantRepository.findMinPricesByColorIds(colorIds).stream()
                        .collect(Collectors.toMap(
                                PriceTuple::colorId,
                                Function.identity(),
                                (a, b) -> a
                        ));
            }
        }

        final ColorFamily finalFilterColor = filterColor;
        final Map<Long, ProductColorImage> imageMap = tmpImageMap;
        final Map<Long, PriceTuple> priceMap = tmpPriceMap;

        return page.map(product -> {
            ProductReviewStats stats = statsMap.get(product.getId());
            ProductListItemResponse base = productMapper.toListItemResponse(product, stats);

            ProductColor mainColor = mainColorMap.get(product.getId());
            Long colorId = (mainColor != null) ? mainColor.getId() : null;

            boolean inStock = inStockProductIds.contains(product.getId());

            String imgUrl = base.imageUrl();
            BigDecimal regularPrice = base.regularPrice();
            BigDecimal salePrice = base.salePrice();

            if (finalFilterColor != null) {
                ProductColorImage image = imageMap.get(product.getId());
                if (image != null) {
                    imgUrl = image.getImageUrl();
                    colorId = image.getColor().getId();

                    PriceTuple price = priceMap.get(colorId);
                    if (price != null) {
                        regularPrice = price.regularPrice();
                        salePrice = price.salePrice();
                    }
                }
            }

            return new ProductListItemResponse(
                    product.getId(),
                    colorId,
                    base.name(),
                    base.slug(),
                    base.brandName(),
                    base.brandSlug(),
                    regularPrice,
                    salePrice,
                    base.averageRating(),
                    base.reviewCount(),
                    imgUrl,
                    base.createdAt(),
                    inStock
            );
        });
    }


    @Override
    @Transactional(readOnly = true)
    public Slice<ProductListItemResponse> getNewProducts(Pageable pageable) {
        Instant from = Instant.now().minus(30, ChronoUnit.DAYS);

        Pageable sorted = PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                Sort.by(Sort.Direction.DESC, "createdAt")
                        .and(Sort.by(Sort.Direction.DESC, "id"))
        );

        Slice<Product> slice = productRepository.findByCreatedAtGreaterThanEqual(from, sorted);

        List<Long> productIds = slice.getContent().stream()
                .map(Product::getId)
                .toList();

        if (productIds.isEmpty()) {
            return new SliceImpl<>(List.of(), pageable, false);
        }

        Map<Long, ProductReviewStats> statsMap =
                statsRepository.findByProductIdIn(productIds).stream()
                        .collect(Collectors.toMap(ProductReviewStats::getProductId, Function.identity(), (a, b) -> a));

        Map<Long, ProductColor> mainColorMap =
                colorRepository.findMainColorsByProductIds(productIds).stream()
                        .collect(Collectors.toMap(pc -> pc.getProduct().getId(), Function.identity(), (a, b) -> a));

        Set<Long> inStockProductIds = new HashSet<>(variantRepository.findInStockProductIds(productIds));

        return slice.map(product -> {
            ProductReviewStats stats = statsMap.get(product.getId());
            ProductListItemResponse base = productMapper.toListItemResponse(product, stats);

            ProductColor mainColor = mainColorMap.get(product.getId());
            Long colorId = (mainColor != null) ? mainColor.getId() : null;

            boolean inStock = inStockProductIds.contains(product.getId());

            return new ProductListItemResponse(
                    product.getId(),
                    colorId,
                    base.name(),
                    base.slug(),
                    base.brandName(),
                    base.brandSlug(),
                    base.regularPrice(),
                    base.salePrice(),
                    base.averageRating(),
                    base.reviewCount(),
                    base.imageUrl(),
                    base.createdAt(),
                    inStock
            );
        });
    }


}
