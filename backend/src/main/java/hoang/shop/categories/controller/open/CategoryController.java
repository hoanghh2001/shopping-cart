package hoang.shop.categories.controller.open;

import hoang.shop.categories.dto.response.CategoryDetailResponse;
import hoang.shop.categories.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {
    private final CategoryService categoryService ;
    @GetMapping("/{slug}")
    public ResponseEntity<CategoryDetailResponse> getActiveCategoryBySlug(@PathVariable String slug) {
        CategoryDetailResponse category = categoryService.getActiveBrandBySlug(slug);
        return ResponseEntity.ok(category);
    }
    @GetMapping
    public ResponseEntity<Slice<CategoryDetailResponse>> findActiveCategory(Pageable pageable) {
        Slice<CategoryDetailResponse> categories = categoryService.findActiveBrands(pageable);
        return ResponseEntity.ok(categories);
    }
}
