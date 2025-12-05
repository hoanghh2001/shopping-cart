package hoang.shop.categories.service;

import lombok.RequiredArgsConstructor;
import hoang.shop.categories.dto.request.ProductVariantCreateRequest;
import hoang.shop.categories.dto.request.ProductVariantUpdateRequest;
import hoang.shop.categories.dto.response.ProductVariantResponse;
import hoang.shop.categories.mapper.ProductVariantMapper;
import hoang.shop.categories.model.Product;
import hoang.shop.categories.model.ProductVariant;
import hoang.shop.categories.repository.ProductRepository;
import hoang.shop.categories.repository.ProductVariantRepository;
import hoang.shop.common.enums.status.ProductVariantStatus;
import hoang.shop.common.exception.DuplicateResourceException;
import hoang.shop.common.exception.NotFoundException;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class ProductVariantServiceImpl implements ProductVariantService {
    private final ProductVariantRepository productVariantRepository;
    private final ProductRepository productRepository;
    private final ProductVariantMapper mapper;
    @Override
    public List<ProductVariantResponse> create(Long productId, List<ProductVariantCreateRequest> requests) {
        if (requests == null || requests.isEmpty()) return List.of();
        if (productId == null) throw new IllegalArgumentException("productId.required");

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new NotFoundException("{error.product.id.not-found}"));
        List<ProductVariant> existing = productVariantRepository.findByProductId(productId);
        Function<String,String> norm = s -> s == null ? "" : s.trim().replace("\"","").toLowerCase();

        Set<String> existingNames = existing.stream()
                .map(v -> norm.apply(v.getName()))
                .collect(Collectors.toSet());
        Set<String> existingColorSize = existing.stream()
                .map(v -> norm.apply(v.getColor()) + "||" + norm.apply(v.getSize()))
                .collect(Collectors.toSet());
        Set<String> seenNames = new HashSet<>();
        Set<String> seenColorSize = new HashSet<>();
        for (ProductVariantCreateRequest r : requests) {
            String name = r.name();
            String cs = (r.color() == null ? "" : r.color()) + "||" + (r.size() == null ? "" : r.size());

            if (name == null || name.isBlank()) throw new IllegalArgumentException("name.required");
            if (!seenNames.add(name))
                throw new DuplicateResourceException("{error.product-variant.variant-name.duplicate} (in request)");
            if (!seenColorSize.add(cs))
                throw new DuplicateResourceException("{error.product-variant.color.size.duplicate} (in request)");
            if (existingNames.contains(name))
                throw new DuplicateResourceException("{error.product-variant.variant-name.duplicate} (exists)");
            if (existingColorSize.contains(cs))
                throw new DuplicateResourceException("{error.product-variant.color.size.duplicate} (exists)");
        }

        List<ProductVariant> toSave = requests.stream()
                .map(req -> {
                    ProductVariant pv = mapper.toEntity(req);
                    pv.setProduct(product);
                    if (pv.getStockQuantity() == null) pv.setStockQuantity(0);
                    return pv;
        }).toList();

        List<ProductVariant> saved = productVariantRepository.saveAll(toSave);

        return saved.stream().map(mapper::toResponse).toList();
    }
    @Override
    public ProductVariantResponse update(Long id, ProductVariantUpdateRequest updateRequest) {
        if (productVariantRepository.existsByNameAndIdNot(updateRequest.variantName(),id))
            throw new DuplicateResourceException("{error.product-variant.name.duplicate}");
        if (productVariantRepository.existsByColorAndSizeAndIdNot(updateRequest.color(),updateRequest.size(),id))
            throw new DuplicateResourceException("{error.product-variant.color.size.duplicate}");
        ProductVariant variant = productVariantRepository.findById(id)
                .orElseThrow(()-> new NotFoundException("error.product-variant.id.notFound"));
        mapper.merger(updateRequest,variant);
        return mapper.toResponse(variant);
    }

    @Override
    public ProductVariantResponse findById(Long id) {
        ProductVariant variant = productVariantRepository.findById(id)
                .orElseThrow(()-> new NotFoundException("error.product-variant.id.notFound"));
        return mapper.toResponse(variant);
    }

    @Override
    public Slice<ProductVariantResponse> findByProductId(Long productId, Pageable pageable) {
        Slice<ProductVariant> page = productVariantRepository.findByProductId(productId, ProductVariantStatus.ACTIVE,pageable);
        return page.map(mapper::toResponse);
    }

    @Override
    public Slice<ProductVariantResponse> findByFilter(
            String keyword,
            String color,
            String size,
            ProductVariantStatus status,
            Long productId,
            BigDecimal minPrice,
            BigDecimal maxPrice,
            Pageable pageable) {
        Slice<ProductVariant> page =productVariantRepository.findByFilter(keyword,color,size,status,productId,minPrice,maxPrice,pageable);
        return page.map(mapper::toResponse);
    }

    @Override
    public boolean deleteById(Long id) {
        return productVariantRepository.updateStatusById(ProductVariantStatus.DELETED,id)>0;
    }

    @Override
    public boolean restoreById(Long id) {
        return productVariantRepository.updateStatusById(ProductVariantStatus.ACTIVE,id)>0;
    }
}
