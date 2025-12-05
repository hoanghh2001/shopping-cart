package hoang.shop.categories.controller.admin;


import hoang.shop.categories.dto.response.AdminCategoryResponse;
import lombok.RequiredArgsConstructor;
import hoang.shop.categories.dto.request.CategoryCreateRequest;
import hoang.shop.categories.dto.request.CategoryUpdateRequest;
import hoang.shop.common.IdListRequest;
import hoang.shop.categories.service.CategoryService;
import hoang.shop.common.enums.CategoryStatus;
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
public class AdminCategoryController {
    private final CategoryService categoryService;

    @PostMapping
    public ResponseEntity<AdminCategoryResponse> create(@RequestBody CategoryCreateRequest createRequest) {
        AdminCategoryResponse saved = categoryService.create(createRequest);
        URI location = URI.create("/api/categories/"+saved.id());
        return ResponseEntity.created(location).body(saved);
    }

    @PutMapping("/{categoryId}")
    public ResponseEntity<AdminCategoryResponse> update(
            @PathVariable(name = "categoryId") Long CategoryId,
            @RequestBody CategoryUpdateRequest updateRequest) {
        AdminCategoryResponse updated =  categoryService.update(CategoryId, updateRequest);
        return ResponseEntity.ok(updated);
    }

    @GetMapping("/{categoryId}")
    public ResponseEntity<AdminCategoryResponse> findById(@PathVariable Long categoryId) {
        AdminCategoryResponse categoryResponse = categoryService.findById(categoryId);
        return ResponseEntity.ok(categoryResponse);
    }
//    @GetMapping("/name/{name}")
//    public ResponseEntity<AdminCategoryResponse> findByName(@PathVariable String name) {
//        AdminCategoryResponse categoryResponse = categoryService.findByName(name);
//        return ResponseEntity.ok().body(categoryResponse);
//    }
//    @GetMapping("/slug/{slug}")
//    public ResponseEntity<AdminCategoryResponse> findBySlug(@PathVariable String slug) {
//        AdminCategoryResponse categoryResponse =  categoryService.findBySlug(slug);
//        return ResponseEntity.ok().body(categoryResponse);
//    }
//    @GetMapping("/product/{id})")
//    public ResponseEntity<Slice<AdminCategoryResponse>> findByProductId(@PathVariable Long id, Pageable pageable){
//        Slice<AdminCategoryResponse> page = categoryService.findByProductId(id,pageable);
//        return ResponseEntity.ok(page);
//    }

    @GetMapping
    public ResponseEntity<Slice<AdminCategoryResponse>> findAllByStatus(
            @RequestParam(required = false) CategoryStatus status,
            @PageableDefault (
                    size = 20,
                    page = 0,
                    sort = "id",
                    direction = Sort.Direction.DESC
            ) Pageable pageable) {

        Slice<AdminCategoryResponse> categoryResponsePage =  categoryService.findAllByStatus(status, pageable);
        return ResponseEntity.ok().body(categoryResponsePage);

    }
//    @PatchMapping("/status/{id}")
//    public ResponseEntity<Void> updateStatusById(
//            @PathVariable Long id,
//            @RequestParam CategoryStatus categoryStatus) {
//        boolean updated = categoryService.updateStatusById(id, categoryStatus);
//        if (!updated) {
//            return ResponseEntity.notFound().build();
//        }
//        return ResponseEntity.noContent().build();
//    }
    @PatchMapping("/{categoryId}/parent/{parentId}")
    public ResponseEntity<AdminCategoryResponse> assignParent(
            @PathVariable Long categoryId,
            @PathVariable Long parentId){
        AdminCategoryResponse response = categoryService.replaceParent(categoryId,parentId);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{categoryId}/parent")
    public ResponseEntity<AdminCategoryResponse> unsetParent(
            @PathVariable Long categoryId){
        AdminCategoryResponse response = categoryService.unsetParent(categoryId);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{categoryId}/products")
    public ResponseEntity<AdminCategoryResponse> addProducts(@PathVariable Long categoryId, @RequestBody IdListRequest ids){
        AdminCategoryResponse response = categoryService.assignProducts(categoryId,ids);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{categoryId}/products")
    public ResponseEntity<AdminCategoryResponse> removeProducts(@PathVariable Long categoryId, @RequestBody IdListRequest ids){
        AdminCategoryResponse response = categoryService.unsetProducts(categoryId,ids);
        return ResponseEntity.ok(response);
    }
}
