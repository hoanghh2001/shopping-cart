package hoang.shop.categories.service;

import lombok.RequiredArgsConstructor;
import hoang.shop.categories.dto.request.IdListRequest;
import hoang.shop.categories.dto.request.ProductCreateRequest;
import hoang.shop.categories.dto.request.ProductUpdateRequest;
import hoang.shop.categories.dto.response.ProductResponse;
import hoang.shop.categories.mapper.ProductMapper;
import hoang.shop.categories.model.Product;
import hoang.shop.categories.repository.ProductRepository;
import hoang.shop.common.enums.status.ProductStatus;
import hoang.shop.common.exception.BadRequestException;
import hoang.shop.common.exception.DuplicateResourceException;
import hoang.shop.common.exception.NotFoundException;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;
    private final ProductMapper mapper;
    @Override
    public ProductResponse create(ProductCreateRequest request) {
        if (productRepository.existsByName(request.name()))
            throw new DuplicateResourceException("{error.product.name.duplicate}");
        if (productRepository.existsBySlug(request.slug()))
            throw new DuplicateResourceException("{error.product.slug.duplicate}");
        Product product = mapper.toEntity(request);
        product = productRepository.save(product);
        return mapper.toResponse(product);
    }

    @Override
    public ProductResponse update(Long productId, ProductUpdateRequest request) {
        if (productRepository.existsByNameAndIdNot(request.name(),productId))
            throw new DuplicateResourceException("{error.product.name.duplicate}");
        if (productRepository.existsBySlugAndIdNot(request.slug(),productId))
            throw new DuplicateResourceException("{error.product.slug.duplicate}");
        Product product = productRepository.findById(productId)
                .orElseThrow(()-> new NotFoundException("{error.product.id.notFound}"));
        mapper.merge(request,product);
        return mapper.toResponse(product);
    }

    @Override
    public ProductResponse findById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(()-> new NotFoundException("{error.product.id.notFound}"));
        return mapper.toResponse(product);
    }

    @Override
    public ProductResponse findByName(String name) {
        Product product = productRepository.findByName(name)
                .orElseThrow(()-> new NotFoundException("{error.product.name.notFound}"));
        return mapper.toResponse(product);
    }

    @Override
    public ProductResponse findBySlug(String slug) {
        Product product = productRepository.findBySlug(slug)
                .orElseThrow(()-> new NotFoundException("{error.product.slug.notFound}"));
        return mapper.toResponse(product);
    }

    @Override
    public Slice<ProductResponse> findByStatus(ProductStatus status, Pageable pageable) {
        Slice<Product> page = productRepository.findByFilter(null,status,null,null,null,null,pageable);
        return page.map(mapper::toResponse);
    }

    @Override
    public Slice<ProductResponse> findByBrandId(Long brandId, Pageable pageable) {
        Slice<Product> page = productRepository.findByBrandId(brandId,pageable);
        return page.map(mapper::toResponse);
    }

    @Override
    public Slice<ProductResponse> findByCategoryId(Long categoryId, Pageable pageable) {
        Slice<Product> page = productRepository.findByCategoryId(categoryId,pageable);
        return page.map(mapper::toResponse);
    }

    @Override
    public Slice<ProductResponse> findByFilter(String keyword,ProductStatus status, Long brandId, Long categoryId, BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable) {
        Slice<Product> page = productRepository.findByFilter(keyword,status,brandId,categoryId,minPrice,maxPrice,pageable);
        return page.map(mapper::toResponse);
    }

    @Override
    public int deleteById(IdListRequest ids) {
        List<Product> productList= productRepository.findAllById(ids.ids());
        if (productList.size()!=ids.ids().size())
            throw new NotFoundException("{error.product.id.notFound}");
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
            throw new NotFoundException("{error.product.id.notFound}");
        boolean isInvalid = productList.stream()
                .anyMatch(p -> p.getStatus()!=ProductStatus.DELETED);
        if (isInvalid)
            throw new BadRequestException("error.product.id.badRequest");
        return productRepository.updateStatusById(ids.ids(),ProductStatus.ACTIVE);
    }




}
