package hoang.shop.categories.controller.admin;


import lombok.RequiredArgsConstructor;
import hoang.shop.categories.dto.request.CategoryCreateRequest;
import hoang.shop.categories.dto.request.CategoryUpdateRequest;
import hoang.shop.categories.dto.request.IdListRequest;
import hoang.shop.categories.dto.response.CategoryResponse;
import hoang.shop.categories.service.CategoryService;
import hoang.shop.common.enums.status.CategoryStatus;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/api/admin/categories")
@RequiredArgsConstructor
public class CategoryController {
    private final CategoryService categoryService;

    @PostMapping
    public ResponseEntity<CategoryResponse> create(@RequestBody CategoryCreateRequest createRequest) {
        CategoryResponse saved = categoryService.create(createRequest);
        URI location = URI.create("/api/categories/"+saved.id());
        return ResponseEntity.created(location).body(saved);
    }
    @PatchMapping("/{id}")
    public ResponseEntity<CategoryResponse> update(
            @PathVariable(name = "id") Long CategoryId,
            @RequestBody CategoryUpdateRequest updateRequest) {
        CategoryResponse updated =  categoryService.update(CategoryId, updateRequest);
        return ResponseEntity.ok(updated);
    }
    @GetMapping("/{id}")
    public ResponseEntity<CategoryResponse> findById(@PathVariable Long id) {
        CategoryResponse categoryResponse = categoryService.findById(id);
        return ResponseEntity.ok(categoryResponse);
    }
    @GetMapping("/name/{name}")
    public ResponseEntity<CategoryResponse> findByName(@PathVariable String name) {
        CategoryResponse categoryResponse = categoryService.findByName(name);
        return ResponseEntity.ok().body(categoryResponse);
    }
    @GetMapping("/slug/{slug}")
    public ResponseEntity<CategoryResponse> findBySlug(@PathVariable String slug) {
        CategoryResponse categoryResponse =  categoryService.findBySlug(slug);
        return ResponseEntity.ok().body(categoryResponse);
    }
    @GetMapping("/product/{id})")
    public ResponseEntity<Slice<CategoryResponse>> findByProductId(@PathVariable Long id, Pageable pageable){
        Slice<CategoryResponse> page = categoryService.findByProductId(id,pageable);
        return ResponseEntity.ok(page);
    }
    @GetMapping
    public ResponseEntity<Slice<CategoryResponse>> findAllByStatus(
            @RequestParam(required = false) CategoryStatus status,
            @PageableDefault (
                    size = 20,
                    page = 0,
                    sort = "id",
                    direction = Sort.Direction.DESC
            ) Pageable pageable) {
        Slice<CategoryResponse> categoryResponsePage =  categoryService.findAllByStatus(status, pageable);
        return ResponseEntity.ok().body(categoryResponsePage);
    }
    @PatchMapping("/status/{id}")
    public ResponseEntity<Void> updateStatusById(
            @PathVariable Long id,
            @RequestParam CategoryStatus categoryStatus) {
        boolean updated = categoryService.updateStatusById(id, categoryStatus);
        if (!updated) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.noContent().build();
    }
    @PatchMapping("/replace/{id}/parent/{parentId}")
    public ResponseEntity<CategoryResponse> replaceParent(@PathVariable Long id,@PathVariable Long parentId){
        CategoryResponse response = categoryService.replaceParent(id,parentId);
        return ResponseEntity.ok(response);
    }
    @DeleteMapping("/unset/{id}")
    public ResponseEntity<CategoryResponse> unsetParent(@PathVariable Long id){
        CategoryResponse response = categoryService.unsetParent(id);
        return ResponseEntity.ok(response);
    }
    @PutMapping("/replace-product/{categoryId}")
    public ResponseEntity<CategoryResponse> replaceProducts(@PathVariable Long categoryId,@RequestBody IdListRequest ids){
        CategoryResponse response = categoryService.replaceProducts(categoryId,ids);
        return ResponseEntity.ok(response);
    }
    @DeleteMapping("/unset-product/{categoryId}")
    public ResponseEntity<CategoryResponse> unsetProducts(@PathVariable Long categoryId,@RequestBody IdListRequest ids){
        CategoryResponse response = categoryService.unsetProducts(categoryId,ids);
        return ResponseEntity.ok(response);
    }
}
