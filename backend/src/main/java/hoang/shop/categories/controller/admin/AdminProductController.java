package hoang.shop.categories.controller.admin;

import lombok.RequiredArgsConstructor;
import hoang.shop.categories.dto.request.IdListRequest;
import hoang.shop.categories.dto.request.ProductCreateRequest;
import hoang.shop.categories.dto.request.ProductUpdateRequest;
import hoang.shop.categories.dto.response.ProductResponse;
import hoang.shop.categories.service.ProductService;
import hoang.shop.common.enums.status.ProductStatus;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.net.URI;

@RestController
@RequestMapping("/api/admin/products")
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;
    @PostMapping
    public ResponseEntity<ProductResponse> create(@RequestBody ProductCreateRequest createRequest) {
        ProductResponse response = productService.create(createRequest);
        URI location = URI.create("/api/products/"+response.id());
        return ResponseEntity.created(location).body(response);
    }
    @PatchMapping("/{id}")
    public ResponseEntity<ProductResponse> update(@PathVariable Long id,@RequestBody ProductUpdateRequest updateRequest) {
         ProductResponse productResponse = productService.update(id, updateRequest);
         return ResponseEntity.ok(productResponse);
    }
    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> findById(@PathVariable Long id) {
        ProductResponse productResponse = productService.findById(id);
        return ResponseEntity.ok(productResponse);
    }
    @GetMapping("/category/{id}")
    public ResponseEntity<Slice<ProductResponse>> findByCategoryId(@PathVariable Long id, Pageable pageable) {
        Slice<ProductResponse> productResponse = productService.findByCategoryId(id, pageable);
        return ResponseEntity.ok(productResponse);
    }
    @GetMapping("/name/{name}")
    public ResponseEntity<ProductResponse> findByName(@PathVariable String name) {
        ProductResponse productResponse = productService.findByName(name);
        return ResponseEntity.ok(productResponse);
    }
    @GetMapping("/slug/{slug}")
    public ResponseEntity<ProductResponse> findBySlug(@PathVariable String slug) {
        ProductResponse productResponse = productService.findBySlug(slug);
        return ResponseEntity.ok(productResponse);
    }
    @GetMapping
    public ResponseEntity<Slice<ProductResponse>> findByStatus(@RequestParam(required = false) ProductStatus status, Pageable pageable) {
        Slice<ProductResponse> page = productService.findByStatus(status, pageable);
        return ResponseEntity.ok(page);
    }
    @GetMapping("/brand-id/{id}")
    public ResponseEntity<Slice<ProductResponse>> findByBrandId(@PathVariable Long id, Pageable pageable) {
        Slice<ProductResponse> page = productService.findByBrandId(id, pageable);
        return ResponseEntity.ok(page);
    }
    @GetMapping("/filter")
    public ResponseEntity<Slice<ProductResponse>> findByFilter(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) ProductStatus status,
            @RequestParam(required = false) Long brandId,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            Pageable pageable) {
        Slice<ProductResponse> page = productService.findByFilter(keyword, status, brandId, categoryId, minPrice, maxPrice, pageable);
        return ResponseEntity.ok(page);
    }
    @PatchMapping("/restore")
    public ResponseEntity<Integer> restoreById(@RequestBody IdListRequest ids) {
        Integer rows = productService.restoreById(ids);
        return ResponseEntity.ok(rows);
    }
    @PatchMapping("/delete")
    public ResponseEntity<Integer> deleteById(@RequestBody IdListRequest ids) {
        Integer rows = productService.deleteById(ids);
        return ResponseEntity.ok(rows);
    }
}
