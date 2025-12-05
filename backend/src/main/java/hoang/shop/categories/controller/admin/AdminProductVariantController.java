package hoang.shop.categories.controller.admin;


import hoang.shop.categories.dto.request.ProductVariantSearchCondition;
import hoang.shop.categories.dto.request.ProductVariantUpdateRequest;
import hoang.shop.categories.dto.response.AdminVariantResponse;
import hoang.shop.categories.service.ProductVariantService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/variants")
@RequiredArgsConstructor
public class AdminProductVariantController {
    private final ProductVariantService variantService;


    @GetMapping
    public ResponseEntity<Page<AdminVariantResponse>> search(
            @ModelAttribute ProductVariantSearchCondition condition ,
            @PageableDefault (direction = Sort.Direction.DESC,sort = "size") Pageable pageable) {
        Page<AdminVariantResponse> variants = variantService.search(condition,pageable);
        return ResponseEntity.ok(variants);
    }

    @GetMapping("/{variantId}")
    public ResponseEntity<AdminVariantResponse> getVariant(
            @PathVariable Long variantId){
        AdminVariantResponse variant = variantService.getVariant(variantId);
        return ResponseEntity.ok(variant);
    }

    @PutMapping("/{variantId}")
    public ResponseEntity<AdminVariantResponse> update(
            @PathVariable Long variantId,
            @RequestBody ProductVariantUpdateRequest request){
        AdminVariantResponse variant = variantService.update(variantId,request);
        return ResponseEntity.ok(variant);
    }

    @PatchMapping("/{variantId}/delete")
    public ResponseEntity<AdminVariantResponse> deleteById(@PathVariable Long variantId) {
        AdminVariantResponse variant = variantService.softDelete(variantId);
        return ResponseEntity.ok(variant);
    }
    @PatchMapping("/{variantId}/restore")
    public ResponseEntity<AdminVariantResponse> restoreById(@PathVariable Long variantId) {
        AdminVariantResponse variant = variantService.restore(variantId);
        return ResponseEntity.ok(variant);
    }

}
