package hoang.shop.categories.controller.admin;

import hoang.shop.categories.dto.request.*;
import hoang.shop.categories.dto.response.AdminColorResponse;
import hoang.shop.categories.dto.response.AdminListItemProductResponse;
import hoang.shop.categories.service.ProductColorService;
import hoang.shop.common.IdListRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import hoang.shop.categories.service.ProductService;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/admin/products")
@RequiredArgsConstructor
public class AdminProductController {
    private final ProductService productService;
    private final ProductColorService productColorService;


    @PostMapping
    public ResponseEntity<AdminListItemProductResponse> create(@RequestBody ProductCreateRequest createRequest) {
        AdminListItemProductResponse product = productService.create(createRequest);
        URI location = URI.create("/api/products/"+product.id());
        return ResponseEntity.created(location).body(product);
    }
    @PutMapping("/{productId}")
    public ResponseEntity<AdminListItemProductResponse> update(@PathVariable Long productId, @RequestBody ProductUpdateRequest updateRequest) {
         AdminListItemProductResponse product = productService.update(productId, updateRequest);
         return ResponseEntity.ok(product);
    }
    @GetMapping("/{productId}")
    public ResponseEntity<AdminListItemProductResponse> findById(@PathVariable Long productId) {
        AdminListItemProductResponse product = productService.getById(productId);
        return ResponseEntity.ok(product);
    }
//    @GetMapping("/name/{name}")
//    public ResponseEntity<AdminListItemProductResponse> findByName(@PathVariable String name) {
//        AdminListItemProductResponse product = productService.getByName(name);
//        return ResponseEntity.ok(product);
//    }

    @GetMapping
    public ResponseEntity<Slice<AdminListItemProductResponse>> search(
            @ModelAttribute ProductSearchCondition condition,
            Pageable pageable) {
        Slice<AdminListItemProductResponse> products = productService.searchForAdmin(condition, pageable);
        return ResponseEntity.ok(products);
    }
    @PatchMapping("/restore")
    public ResponseEntity<Integer> restore(@RequestBody IdListRequest ids) {
        Integer rows = productService.restoreById(ids);
        return ResponseEntity.ok(rows);
    }
    @PatchMapping("/delete")
    public ResponseEntity<Integer> sortDelete(@RequestBody IdListRequest ids) {
        Integer rows = productService.deleteById(ids);
        return ResponseEntity.ok(rows);
    }
    @PostMapping("/{productId}/colors")
    public ResponseEntity<AdminColorResponse> addColor(
            @PathVariable Long productId,
            @RequestBody @Valid ProductColorCreateRequest request
    ) {
        AdminColorResponse color =  productColorService.addColor(productId, request);
        URI location = URI.create("/api/variants/"+color.id());
        return ResponseEntity.created(location).body(color);
    }
    @GetMapping("/{productId}/colors")
    public ResponseEntity<List<AdminColorResponse>> getVariants(
            @PathVariable Long productId
    ) {
        List<AdminColorResponse> colors =  productColorService.getColorByProductId(productId);
        return ResponseEntity.ok(colors);
    }


}
