package hoang.shop.categories.service;

import hoang.shop.categories.dto.request.BrandCreateRequest;
import hoang.shop.categories.dto.request.BrandUpdateRequest;
import hoang.shop.categories.dto.response.BrandResponse;
import hoang.shop.common.enums.status.BrandStatus;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

public interface BrandService {
    //create
    BrandResponse create(BrandCreateRequest createRequest);
    //update
    BrandResponse update(Long id, BrandUpdateRequest updateRequest);
    void updateStatusById(Long id,BrandStatus status);
    //read
    BrandResponse findById(Long id);

    BrandResponse findByName(String name);

    BrandResponse findBySlug(String slug);

    Slice<BrandResponse> findByStatus(BrandStatus status, Pageable pageable);
}
