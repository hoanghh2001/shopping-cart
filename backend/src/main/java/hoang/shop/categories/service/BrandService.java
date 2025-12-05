package hoang.shop.categories.service;

import hoang.shop.categories.dto.request.BrandCreateRequest;
import hoang.shop.categories.dto.request.BrandUpdateRequest;
import hoang.shop.categories.dto.response.AdminBrandResponse;
import hoang.shop.categories.dto.response.BrandResponse;
import hoang.shop.common.enums.BrandStatus;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

public interface BrandService {
    //Admin

    AdminBrandResponse create(BrandCreateRequest createRequest);
    AdminBrandResponse update(Long id, BrandUpdateRequest updateRequest);
    AdminBrandResponse findById(Long id);
    AdminBrandResponse findByName(String name);
    AdminBrandResponse findBySlug(String slug);
    Slice<AdminBrandResponse> findByStatus(BrandStatus status, Pageable pageable);
    AdminBrandResponse softDelete(Long brandId);
    AdminBrandResponse restore(Long brandId);

    //Public
    Slice<BrandResponse> findActiveBrands(Pageable pageable);
    BrandResponse getActiveBrandById(Long brandId);
    BrandResponse getActiveBrandBySlug(String slug);


}
