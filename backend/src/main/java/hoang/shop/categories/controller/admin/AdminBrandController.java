package hoang.shop.categories.controller.admin;


import hoang.shop.categories.dto.response.AdminBrandResponse;
import lombok.RequiredArgsConstructor;
import hoang.shop.categories.dto.request.BrandCreateRequest;
import hoang.shop.categories.dto.request.BrandUpdateRequest;
import hoang.shop.categories.dto.response.BrandResponse;
import hoang.shop.categories.service.BrandService;
import hoang.shop.common.enums.BrandStatus;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/api/admin/brands")
@RequiredArgsConstructor
public class AdminBrandController {
    private final BrandService brandService;
    @PostMapping
    public ResponseEntity<AdminBrandResponse> create(@RequestBody BrandCreateRequest createRequest) {
        AdminBrandResponse brandResponse =  brandService.create(createRequest);
        URI location = URI.create("/api/brands/"+brandResponse.id());
        return ResponseEntity.created(location).body(brandResponse);
    }
    @PatchMapping("/{brandId}/delete")
    public ResponseEntity<AdminBrandResponse> softDelete(@PathVariable Long brandId) {
        AdminBrandResponse brand = brandService.softDelete(brandId);
        return ResponseEntity.ok(brand);
    }
    @PatchMapping("/{brandId}/restore")
    public ResponseEntity<AdminBrandResponse> restore(@PathVariable Long brandId) {
        AdminBrandResponse brand = brandService.restore(brandId);
        return ResponseEntity.ok(brand);
    }

    @PutMapping("/{brandId}")
    public ResponseEntity<AdminBrandResponse> update(@PathVariable Long brandId,@RequestBody BrandUpdateRequest updateRequest) {
        AdminBrandResponse updated = brandService.update(brandId, updateRequest);
        return ResponseEntity.ok(updated);
    }

    @GetMapping("/{brandId}")
    public ResponseEntity<AdminBrandResponse> findById(@PathVariable Long brandId) {
        AdminBrandResponse brandResponse =  brandService.findById(brandId);
        return ResponseEntity.ok(brandResponse);
    }
//    @GetMapping("/name")
//    public ResponseEntity<AdminBrandResponse> findByName(@RequestParam String name) {
//        AdminBrandResponse brandResponse = brandService.findByName(name);
//        return ResponseEntity.ok(brandResponse);
//    }
//    @GetMapping("/slug")
//    public ResponseEntity<AdminBrandResponse> findBySlug(@RequestParam String slug) {
//        AdminBrandResponse brandResponse = brandService.findBySlug(slug);
//        return ResponseEntity.ok(brandResponse);
//    }
    @GetMapping
    public ResponseEntity<Slice<AdminBrandResponse>> findByStatus(
            @RequestParam(required = false) BrandStatus status,
            @PageableDefault(
                    page = 0,
                    size = 15,
                    sort = {"createdAt","id"},
                    direction = Sort.Direction.DESC
            ) Pageable pageable) {
        Slice<AdminBrandResponse> brandResponsePage =  brandService.findByStatus(status, pageable);
        return ResponseEntity.ok(brandResponsePage);
    }
}
