package hoang.shop.categories.controller.admin;


import hoang.shop.categories.dto.request.IdListRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import hoang.shop.categories.dto.request.ProductImageCreateRequest;
import hoang.shop.categories.dto.request.ProductImageUpdateRequest;
import hoang.shop.categories.dto.response.ProductImageResponse;
import hoang.shop.categories.service.ProductImageService;
import hoang.shop.common.enums.status.ProductImageStatus;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/api/admin/products/{productId}/images")
@RequiredArgsConstructor
public class AdminProductImageController {
    private final ProductImageService imageService;

    @PostMapping
    public ResponseEntity<Map<String, Object>> createImages(
            @PathVariable Long productId,
            @RequestBody @Valid List<ProductImageCreateRequest> requestList) {

        List<ProductImageResponse> items = imageService.createImages(productId, requestList);

        List<Long> createdIds = items.stream()
                .map(ProductImageResponse::id)
                .toList();

        List<String> createdUris = createdIds.stream()
                .map(id -> ServletUriComponentsBuilder.fromCurrentRequest()
                        .path("/{id}")
                        .buildAndExpand(id)
                        .toUriString())
                .toList();

        URI location = createdIds.isEmpty()
                ? ServletUriComponentsBuilder.fromCurrentRequest().build().toUri()
                : ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(createdIds.get(0))
                .toUri();

        Map<String, Object> body = Map.of(
                "createdCount", createdIds.size(),
                "createdIds", createdIds,
                "createdUris", createdUris,
                "items", items
        );

        return ResponseEntity.created(location).body(body);
    }
    @PatchMapping
    public ResponseEntity<ProductImageResponse> updateImage(@PathVariable Long productId,@PathVariable Long imageId,@RequestBody ProductImageUpdateRequest updateRequest) {
        ProductImageResponse productImageResponse =  imageService.updateImage(productId,imageId, updateRequest);
        return ResponseEntity.ok(productImageResponse);
    }

    @GetMapping
    public ResponseEntity<List<ProductImageResponse>> findImages(
            @PathVariable Long productId,
            @RequestParam(required = false)ProductImageStatus status,
            @RequestParam(required = false) Boolean isMain,
            Pageable pageable){
        List<ProductImageResponse> images = imageService.findImages(productId,status,isMain,pageable);
        return ResponseEntity.ok(images);
    }
    @GetMapping("/{imageId}")
    public ResponseEntity<ProductImageResponse> getImageById(@PathVariable Long imageId) {
        ProductImageResponse productImageResponse =  imageService.getImageById(imageId);
        return ResponseEntity.ok(productImageResponse);
    }

    @DeleteMapping("/{imageId}")
    public ResponseEntity<Void> deleteImage(@PathVariable Long productId,@PathVariable Long imageId) {
        imageService.deleteImage(productId, imageId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteImages(@PathVariable Long productId,@RequestBody IdListRequest ids) {
        imageService.deleteImages(productId, ids);
        return ResponseEntity.noContent().build();
    }
}
