package hoang.shop.categories.service;

import hoang.shop.categories.dto.request.ProductVariantSearchCondition;
import hoang.shop.categories.dto.response.AdminVariantResponse;
import hoang.shop.categories.dto.response.VariantResponse;
import hoang.shop.categories.model.ProductColor;
import hoang.shop.categories.repository.ProductColorRepository;
import hoang.shop.categories.spec.ProductVariantSpec;
import lombok.RequiredArgsConstructor;
import hoang.shop.categories.dto.request.ProductVariantCreateRequest;
import hoang.shop.categories.dto.request.ProductVariantUpdateRequest;
import hoang.shop.categories.mapper.VariantMapper;
import hoang.shop.categories.model.ProductVariant;
import hoang.shop.categories.repository.ProductRepository;
import hoang.shop.categories.repository.ProductVariantRepository;
import hoang.shop.common.enums.ProductVariantStatus;
import hoang.shop.common.exception.NotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Transactional
@RequiredArgsConstructor
public class ProductVariantServiceImpl implements ProductVariantService {
    private final ProductVariantRepository productVariantRepository;
    private final ProductColorRepository colorRepository;
    private final VariantMapper variantMapper;


    @Override
    public List<AdminVariantResponse> getVariantsByColorId(Long colorId) {
        if (!colorRepository.existsById(colorId))
                throw new NotFoundException("error.product-variant.color-id.not-found");
        List<ProductVariant> variants = productVariantRepository.findByColor_Id(colorId);
        return variants.stream().map(variantMapper::toAdminResponse).toList();
    }

    @Override
    public AdminVariantResponse getVariant(Long variantId) {
        ProductVariant variant = productVariantRepository.findById(variantId)
                .orElseThrow(()-> new NotFoundException("error.product-variant.id.not-found"));
        return variantMapper.toAdminResponse(variant);
    }

    @Override
    public Page<AdminVariantResponse> search(ProductVariantSearchCondition condition, Pageable pageable) {
        Specification<ProductVariant> spec = ProductVariantSpec.build(condition);
        Page<ProductVariant> products = productVariantRepository.findAll(spec,pageable);
        return products.map(variantMapper::toAdminResponse);
    }

    @Override
    public AdminVariantResponse softDelete(Long variantId) {
        ProductVariant variant = productVariantRepository.findByIdAndStatus(variantId,ProductVariantStatus.ACTIVE)
                .orElseThrow(()-> new NotFoundException("error.product-variant.id.not-found"));
        variant.setStatus(ProductVariantStatus.DELETED);
        return variantMapper.toAdminResponse(variant);
    }

    @Override
    public AdminVariantResponse restore(Long variantId) {
        ProductVariant variant = productVariantRepository.findByIdAndStatus(variantId,ProductVariantStatus.DELETED)
                .orElseThrow(()-> new NotFoundException("error.product-variant.id.not-found"));
        variant.setStatus(ProductVariantStatus.ACTIVE);
        return variantMapper.toAdminResponse(variant);
    }

    @Override
    public AdminVariantResponse createVariant(Long colorId, ProductVariantCreateRequest request) {
        ProductColor color = colorRepository.findById(colorId)
                .orElseThrow(() -> new NotFoundException("{error.product-color.id.not-found}"));
        ProductVariant variant = variantMapper.toEntity(request);
        variant.setColor(color);
        color.getVariants().add(variant);
        ProductVariant saved = productVariantRepository.save(variant);
        return variantMapper.toAdminResponse(saved);
    }

    @Override
    public AdminVariantResponse update(Long variantId, ProductVariantUpdateRequest updateRequest) {
        ProductVariant variant = productVariantRepository.findById(variantId)
                .orElseThrow(()-> new NotFoundException("error.product-variant.id.not-found"));
        variantMapper.merger(updateRequest,variant);
        productVariantRepository.save(variant);
        return variantMapper.toAdminResponse(variant);
    }
}
