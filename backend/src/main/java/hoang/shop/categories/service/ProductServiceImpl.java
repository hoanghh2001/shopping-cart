package hoang.shop.categories.service;

import com.github.slugify.Slugify;
import hoang.shop.categories.dto.request.*;
import hoang.shop.categories.dto.response.AdminListItemProductResponse;
import hoang.shop.categories.dto.response.ProductDetailResponse;
import hoang.shop.categories.dto.response.ProductListItemResponse;
import hoang.shop.categories.model.*;
import hoang.shop.categories.repository.*;
import hoang.shop.categories.spec.ProductSpec;
import hoang.shop.common.IdListRequest;
import lombok.RequiredArgsConstructor;
import hoang.shop.categories.mapper.ProductMapper;
import hoang.shop.common.enums.ProductStatus;
import hoang.shop.common.exception.BadRequestException;
import hoang.shop.common.exception.DuplicateResourceException;
import hoang.shop.common.exception.NotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;
import java.util.Optional;

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

    @Override
    public AdminListItemProductResponse create(ProductCreateRequest request) {


        if (productRepository.existsByName(request.name()))
            throw new DuplicateResourceException("{error.product.name.duplicate}");
        String name =request.name().trim().toLowerCase(Locale.ROOT);
        String slug = Slugify.builder().build().slugify(request.name());
        if (productRepository.existsBySlug(slug))
            throw new DuplicateResourceException("{error.product.slug.duplicate}");
        Product product = productMapper.toEntity(request);
        if (request.brandId()!=null) {
            Brand brand = brandRepository.findById(request.brandId())
                    .orElseThrow(()-> new NotFoundException("{error.brand.id.not-found}"));
            product.setBrand(brand);
        }
        if(request.categoryId()!=null){
            Category category = categoryRepository.findById(request.categoryId())
                    .orElseThrow(()-> new NotFoundException("{error.category.id.not-found}"));
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
        if (productRepository.existsByNameAndIdNot(request.name(),productId))
            throw new DuplicateResourceException("{error.product.name.duplicate}");
        if (productRepository.existsBySlugAndIdNot(request.slug(),productId))
            throw new DuplicateResourceException("{error.product.slug.duplicate}");
        Product product = productRepository.findById(productId)
                .orElseThrow(()-> new NotFoundException("{error.product.id.not-found}"));
        productMapper.merge(request,product);
        return productMapper.toAdminListItemResponse(product);
    }

    @Override
    public AdminListItemProductResponse getById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(()-> new NotFoundException("{error.product.id.not-found}"));
        return productMapper.toAdminListItemResponse(product);
    }

    @Override
    public AdminListItemProductResponse getByName(String name) {
        Product product = productRepository.findByName(name)
                .orElseThrow(()-> new NotFoundException("{error.product.name.not-found}"));
        return productMapper.toAdminListItemResponse(product);
    }

    @Override
    public Page<AdminListItemProductResponse> searchForAdmin(ProductSearchCondition condition, Pageable pageable) {
        Specification<Product> spec = ProductSpec.build(condition);
        Page<Product> page = productRepository.findAll(spec,pageable);
        return page.map(productMapper::toAdminListItemResponse);
    }

    @Override
    public int deleteById(IdListRequest ids) {
        List<Product> productList= productRepository.findAllById(ids.ids());
        if (productList.size()!=ids.ids().size())
            throw new NotFoundException("{error.product.id.not-found}");
        boolean isInvalid = productList.stream()
                .anyMatch(p -> p.getStatus()!=ProductStatus.ACTIVE);
        if (isInvalid)
            throw new BadRequestException("error.product.id.badRequest");
        return productRepository.updateStatusById(ids.ids(),ProductStatus.DELETED);
    }

    @Override
    public int restoreById(IdListRequest ids) {
        List<Product> productList= productRepository.findAllById(ids.ids());
        if (productList.size()!=ids.ids().size())
            throw new NotFoundException("{error.product.id.not-found}");
        boolean isInvalid = productList.stream()
                .anyMatch(p -> p.getStatus()!=ProductStatus.DELETED);
        if (isInvalid)
            throw new BadRequestException("error.product.id.badRequest");
        return productRepository.updateStatusById(ids.ids(),ProductStatus.ACTIVE);
    }

    @Override
    public ProductDetailResponse getActiveBySlug(String slug) {
        Product product = productRepository.findBySlugAndStatus(slug,ProductStatus.ACTIVE)
                .orElseThrow(()-> new NotFoundException("{error.product.slug.not-found}"));

        ProductReviewStats stats = statsRepository.findByProductId(product.getId())
                .orElseThrow(()-> new NotFoundException("{error.product-review-stats.product-id.not-found}"));

        return productMapper.toDetailResponse(product,stats);
    }


    @Override
    public Page<ProductListItemResponse> search(PublicProductSearchCondition condition, Pageable pageable) {
        Specification<Product> spec = ProductSpec.buildPublic(condition);

        return productRepository.findAll(spec, pageable)
                .map(product -> {
                    ProductReviewStats stats = statsRepository.findByProductId(product.getId())
                            .orElseThrow(()-> new NotFoundException("{error.product-review-stats.product-id.not-found}"));
                    ProductListItemResponse base = productMapper.toListItemResponse(product,stats);

                    ProductTag productTag = productTagRepository
                            .findFirstByProduct_IdAndMainTrue(product.getId())
                            .orElse(null);

                    Tag mainTag = Optional.ofNullable(productTag).map(ProductTag::getTag).orElse(null);
                    String mainTagName = (mainTag != null)? mainTag.getName():null;
                    String mainTagSlug = (mainTag!= null) ? mainTag.getSlug() : null;

                    ProductColor color = colorRepository.findFirstByProduct_IdAndMainTrue(product.getId())
                            .orElse(null);
                    Long colorId = color !=null ? color.getId() : null;
                    boolean productInStock = product.getColors().stream()
                            .flatMap(c -> c.getVariants().stream())
                            .anyMatch(v -> v.getStock() > 0);
                    return new ProductListItemResponse(
                            colorId,
                            base.name(),
                            base.slug(),
                            base.brandName(),
                            base.brandSlug(),
                            mainTagName,
                            mainTagSlug,
                            base.minPrice(),
                            base.maxPrice(),
                            base.averageRating(),
                            base.reviewCount(),
                            base.imageUrl(),
                            productInStock

                    );
                });
    }


}
