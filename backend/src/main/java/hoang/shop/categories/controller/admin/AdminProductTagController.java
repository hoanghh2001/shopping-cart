package hoang.shop.categories.controller.admin;

import hoang.shop.categories.dto.response.AdminListItemProductResponse;
import lombok.RequiredArgsConstructor;
import hoang.shop.categories.service.ProductTagService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/products/{productId}/tags")
@RequiredArgsConstructor
public class AdminProductTagController {
    private final ProductTagService productTagService;
    @PostMapping
    public ResponseEntity<AdminListItemProductResponse> attachTagsToProduct(@PathVariable Long productId, @RequestBody List<Long> tagIds) {
        AdminListItemProductResponse productResponse = productTagService.attachTagsToProduct(productId, tagIds);
        return ResponseEntity.ok(productResponse);
    }
    @DeleteMapping
    public ResponseEntity<AdminListItemProductResponse> detachTagsFromProduct(@PathVariable Long productId, @RequestBody List<Long> tagIds) {
        AdminListItemProductResponse productResponse = productTagService.detachTagsFromProduct(productId, tagIds);
        return ResponseEntity.ok(productResponse);
    }

}
