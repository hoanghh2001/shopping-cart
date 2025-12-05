package hoang.shop.categories.service;

import hoang.shop.categories.dto.request.CategoryCreateRequest;
import hoang.shop.categories.dto.request.CategoryUpdateRequest;
import hoang.shop.categories.dto.request.IdListRequest;
import hoang.shop.categories.dto.response.CategoryResponse;
import hoang.shop.common.enums.status.CategoryStatus;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

public interface CategoryService {
    //create
    CategoryResponse create(CategoryCreateRequest createRequest);
    //update
    CategoryResponse update(Long categoryId, CategoryUpdateRequest updateRequest);
    CategoryResponse replaceParent(Long categoryId, Long parentId);
    CategoryResponse unsetParent(Long categoryId);
    //read
    CategoryResponse findById(Long id);
    CategoryResponse findByName(String name);
    CategoryResponse findBySlug(String slug);
    //read list
    Slice<CategoryResponse> findAllByStatus(CategoryStatus status, Pageable pageable);
    //update status
    boolean updateStatusById(Long id , CategoryStatus categoryStatus);


    Slice<CategoryResponse> findByProductId(Long id, Pageable pageable);

    CategoryResponse replaceProducts(Long categoryId, IdListRequest ids);

    CategoryResponse unsetProducts(Long categoryId, IdListRequest ids);
}
