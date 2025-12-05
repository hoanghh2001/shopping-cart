package hoang.shop.categories.controller.admin;

import lombok.RequiredArgsConstructor;
import hoang.shop.categories.dto.response.ProductResponse;
import hoang.shop.categories.dto.response.TagResponse;
import hoang.shop.categories.service.ProductTagService;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductTagController {
    private final ProductTagService productTagService;
    @PostMapping("/{productId}")
    public ResponseEntity<ProductResponse> attachTagsToProduct(@PathVariable Long productId, @RequestBody List<Long> tagIds) {
        ProductResponse productResponse = productTagService.attachTagsToProduct(productId, tagIds);
        URI location = URI.create("/api/product-tag/"+productResponse.id());
        return ResponseEntity.created(location).body(productResponse);
    }
    @DeleteMapping("/{productId}/tags")
    public ResponseEntity<ProductResponse> detachTagsFromProduct(@PathVariable Long productId,@RequestBody List<Long> tagIds) {
        ProductResponse productResponse = productTagService.detachTagsFromProduct(productId, tagIds);
        return ResponseEntity.ok(productResponse);
    }
    @GetMapping("/{productId}/tags")
    public ResponseEntity<Slice<TagResponse>> findTagsByProductId(@PathVariable Long productId, Pageable pageable) {
        Slice<TagResponse> slice = productTagService.findTagsByProductId(productId, pageable);
        return ResponseEntity.ok(slice);
    }
    @GetMapping("/tag/{tagId}")
    public ResponseEntity<Slice<ProductResponse>> findProductsByTagId(@PathVariable Long tagId, Pageable pageable) {
        Slice<ProductResponse> slice = productTagService.findProductsByTagId(tagId, pageable);
        return ResponseEntity.ok(slice);

    }
}
