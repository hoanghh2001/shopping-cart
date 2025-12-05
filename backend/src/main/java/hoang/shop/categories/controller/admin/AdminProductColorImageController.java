package hoang.shop.categories.controller.admin;


import hoang.shop.categories.dto.request.ProductColorImageCreateRequest;
import hoang.shop.categories.dto.response.AdminImageResponse;
import hoang.shop.categories.service.ProductColorImageService;
import hoang.shop.common.IdListRequest;
import hoang.shop.categories.dto.request.ProductColorImageUpdateRequest;
import hoang.shop.common.enums.ImageStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/admin/images")
@RequiredArgsConstructor
public class AdminProductColorImageController {
    private final ProductColorImageService imageService;

    @GetMapping
    public List<AdminImageResponse> search(
            @RequestParam(name = "id",required = false) Long colorId,
            @RequestParam(required = false) ImageStatus status,
            @RequestParam(required = false) Boolean isMain,
            Pageable pageable) {
        return imageService.search(colorId, status, isMain, pageable);
    }

    @PutMapping(value = "/{imageId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public AdminImageResponse updateImage(
            @PathVariable Long imageId,
            @RequestPart("file") MultipartFile file,
            @RequestPart("metadata") ProductColorImageUpdateRequest updateRequest) {
        return imageService.updateImage(imageId, file,updateRequest);
    }
    @PatchMapping("/{imageId}/delete")
    public ResponseEntity<Void> softDeleteImage(@PathVariable Long imageId) {
        imageService.softDeleteImage(imageId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{imageId}/restore")
    public ResponseEntity<Void> restoreImage(@PathVariable Long imageId) {
        imageService.restoreImage(imageId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{imageId}/bulk-delete")
    public ResponseEntity<Void> deleteImages(@PathVariable Long imageId, @RequestBody IdListRequest ids) {
        imageService.deleteImages(imageId, ids.ids());
        return ResponseEntity.noContent().build();
    }
}
