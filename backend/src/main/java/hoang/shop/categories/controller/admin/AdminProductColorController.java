package hoang.shop.categories.controller.admin;

import hoang.shop.categories.dto.request.*;
import hoang.shop.categories.dto.response.AdminImageResponse;
import hoang.shop.categories.dto.response.AdminVariantResponse;
import hoang.shop.categories.dto.response.AdminColorResponse;
import hoang.shop.categories.service.ProductColorImageService;
import hoang.shop.categories.service.ProductColorService;
import hoang.shop.categories.service.ProductVariantService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/admin/colors")
@RequiredArgsConstructor
public class AdminProductColorController {
    private final ProductColorService productColorService;
    private final ProductVariantService variantService;
    private final ProductColorImageService imageService;

    @PutMapping("/{colorId}")
    public AdminColorResponse updateColor(
            @PathVariable Long colorId,
            @RequestBody @Valid ProductColorUpdateRequest request) {
        return productColorService.updateColor(colorId, request);
    }
    @PatchMapping("/{colorId}/delete")
    public ResponseEntity<AdminColorResponse> deleteColor(@PathVariable Long colorId) {
        AdminColorResponse color = productColorService.deleteColor(colorId);
        return ResponseEntity.ok(color);
    }

    @PatchMapping("/{colorId}/restore")
    public ResponseEntity<AdminColorResponse> restoreColor(@PathVariable Long colorId) {
        AdminColorResponse color = productColorService.restoreColor(colorId);
        return ResponseEntity.ok(color);
    }

    @PostMapping("/{colorId}/variants")
    public ResponseEntity<AdminVariantResponse> addVariant(
            @PathVariable Long colorId,
            @RequestBody ProductVariantCreateRequest request){
        AdminVariantResponse variants = variantService.createVariant(colorId,request);
        URI location = URI.create("/api/variants/"+variants.id());
        return ResponseEntity.created(location).body(variants);
    }

    @GetMapping("/{colorId}/variants")
    public ResponseEntity<List<AdminVariantResponse>> getVariants(
            @PathVariable Long colorId) {
        List<AdminVariantResponse> variants = variantService.getVariantsByColorId(colorId);
        return ResponseEntity.ok(variants);
    }

    @PostMapping(value = "/{colorId}/images", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<List<AdminImageResponse>> uploadImages(
            @PathVariable Long colorId,
            @RequestPart("files") List<MultipartFile> files,
            @RequestPart("metadata") List<ProductColorImageCreateRequest> metadataList
    ) {

        List<AdminImageResponse> items = imageService.uploadColorImage(colorId, files,metadataList);

        return ResponseEntity.ok(items);
    }
}
