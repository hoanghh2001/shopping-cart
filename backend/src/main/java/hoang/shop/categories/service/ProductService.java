package hoang.shop.categories.service;

import hoang.shop.categories.dto.request.IdListRequest;
import hoang.shop.categories.dto.request.ProductCreateRequest;
import hoang.shop.categories.dto.request.ProductUpdateRequest;
import hoang.shop.categories.dto.response.ProductResponse;
import hoang.shop.common.enums.status.ProductStatus;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import java.math.BigDecimal;

public interface ProductService {
    ProductResponse create(ProductCreateRequest createRequest);
    ProductResponse update(Long productId, ProductUpdateRequest updateRequest);
    ProductResponse findById(Long productId);
    ProductResponse findByName(String name);
    ProductResponse findBySlug(String slug);
    Slice<ProductResponse> findByFilter(
            String keyword,
            ProductStatus status,
            Long tagId,
            Long brandId,
            Long categoryId,
            BigDecimal minPrice,
            BigDecimal maxPrice,
            Pageable pageable);
    int deleteById(IdListRequest ids);
    int restoreById(IdListRequest ids);

    <ProductResponse> findActiveProducts();
    ProductResponse findActiveBySlug();
    ProductResponse findActiveById();

}
