package hoang.shop.categories.service;

import hoang.shop.categories.dto.request.ProductVariantCreateRequest;
import hoang.shop.categories.dto.request.ProductVariantSearchCondition;
import hoang.shop.categories.dto.request.ProductVariantUpdateRequest;
import hoang.shop.categories.dto.response.AdminVariantResponse;
import hoang.shop.categories.dto.response.VariantResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ProductVariantService {
    AdminVariantResponse getVariant(Long variantId);

    Page<AdminVariantResponse> search(
            ProductVariantSearchCondition condition,
            Pageable pageable
    );
    AdminVariantResponse softDelete(Long variantId);
    AdminVariantResponse restore(Long variantId);
    AdminVariantResponse createVariant(Long colorId, ProductVariantCreateRequest request);
    AdminVariantResponse update(Long variantId, ProductVariantUpdateRequest updateRequest);

    List<AdminVariantResponse> getVariantsByColorId(Long colorId);

}
