package hoang.shop.categories.service;

import hoang.shop.categories.dto.request.ProductVariantCreateRequest;
import hoang.shop.categories.dto.request.ProductVariantUpdateRequest;
import hoang.shop.categories.dto.response.ProductVariantResponse;
import hoang.shop.common.enums.status.ProductVariantStatus;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import java.util.List;

public interface ProductVariantService {
    //create update
    List<ProductVariantResponse> create(Long productId,List<ProductVariantCreateRequest> createRequest);
    ProductVariantResponse update(Long id, ProductVariantUpdateRequest updateRequest);
    //Read && List
    ProductVariantResponse findById(Long id);
    Slice<ProductVariantResponse> findByProductId(Long productId, Pageable pageable);
    Slice<ProductVariantResponse> findByFilter(
            String keyword,
            String color,
            String size,
            ProductVariantStatus status,
            Long productId,
            java.math.BigDecimal minPrice,
            java.math.BigDecimal maxPrice,
            Pageable pageable
    );
    //delete && restore
    boolean deleteById(Long id);
    boolean restoreById(Long id);
}
