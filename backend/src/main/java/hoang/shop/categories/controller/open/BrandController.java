package hoang.shop.categories.controller.open;

import hoang.shop.categories.dto.response.BrandResponse;
import hoang.shop.categories.service.BrandService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/brands")
@RequiredArgsConstructor
public class BrandController {
    private final BrandService brandService ;

    @GetMapping
    public ResponseEntity<Slice<BrandResponse>> findActiveBrands(Pageable pageable) {
        Slice<BrandResponse> brands = brandService.findActiveBrands(pageable);
        return ResponseEntity.ok(brands);
    }

    @GetMapping("/{slug}")
    public ResponseEntity<BrandResponse> getActiveBrandBySlug(@PathVariable String slug) {
        BrandResponse brand = brandService.getActiveBrandBySlug(slug);
        return ResponseEntity.ok(brand);
    }

}
