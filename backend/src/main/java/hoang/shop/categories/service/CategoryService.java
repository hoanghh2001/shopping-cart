package hoang.shop.categories.service;

import hoang.shop.categories.dto.request.CategoryCreateRequest;
import hoang.shop.categories.dto.request.CategoryUpdateRequest;
import hoang.shop.categories.dto.response.AdminCategoryResponse;
import hoang.shop.categories.dto.response.CategoryDetailResponse;
import hoang.shop.common.IdListRequest;
import hoang.shop.common.enums.CategoryStatus;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

public interface CategoryService {

    //Admin
    AdminCategoryResponse create(CategoryCreateRequest createRequest);
    AdminCategoryResponse update(Long categoryId, CategoryUpdateRequest updateRequest);
    AdminCategoryResponse replaceParent(Long categoryId, Long parentId);
    AdminCategoryResponse unsetParent(Long categoryId);
    AdminCategoryResponse findById(Long id);
    AdminCategoryResponse findByName(String name);
    AdminCategoryResponse findBySlug(String slug);
    Slice<AdminCategoryResponse> findAllByStatus(CategoryStatus status, Pageable pageable);
    boolean updateStatusById(Long id , CategoryStatus categoryStatus);
    Slice<AdminCategoryResponse> findByProductId(Long id, Pageable pageable);
    AdminCategoryResponse assignProducts(Long categoryId, IdListRequest ids);
    AdminCategoryResponse unsetProducts(Long categoryId, IdListRequest ids);

    //User
    CategoryDetailResponse getActiveBrandBySlug(String slug);
    CategoryDetailResponse getActiveBrandById(Long brandId);
    Slice<CategoryDetailResponse> findActiveBrands(Pageable pageable);
}
