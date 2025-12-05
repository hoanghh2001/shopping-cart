package hoang.shop.categories.service;

import hoang.shop.categories.dto.request.*;
import hoang.shop.categories.dto.response.AdminListItemProductResponse;
import hoang.shop.categories.dto.response.ProductDetailResponse;
import hoang.shop.categories.dto.response.ProductListItemResponse;
import hoang.shop.common.IdListRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProductService {
    AdminListItemProductResponse create(ProductCreateRequest createRequest);
    AdminListItemProductResponse update(Long productId, ProductUpdateRequest updateRequest);
    AdminListItemProductResponse getById(Long productId);
    AdminListItemProductResponse getByName(String name);
    Page<AdminListItemProductResponse> searchForAdmin(
            ProductSearchCondition condition,
            Pageable pageable);
    int deleteById(IdListRequest ids);
    int restoreById(IdListRequest ids);

    ProductDetailResponse getActiveBySlug(String slug);
    Page<ProductListItemResponse> search(PublicProductSearchCondition condition, Pageable pageable);

}
