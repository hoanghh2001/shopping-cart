package hoang.shop.categories.controller.open;

import hoang.shop.categories.dto.request.ProductReviewCreateRequest;
import hoang.shop.categories.dto.request.PublicProductSearchCondition;
import hoang.shop.categories.dto.response.ColorResponse;
import hoang.shop.categories.dto.response.ProductDetailResponse;
import hoang.shop.categories.dto.response.ProductListItemResponse;
import hoang.shop.categories.dto.response.ProductReviewResponse;
import hoang.shop.categories.service.ProductColorService;
import hoang.shop.categories.service.ProductReviewService;
import hoang.shop.categories.service.ProductService;
import hoang.shop.categories.service.ProductVariantService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;
    private final ProductColorService colorService;
    ProductReviewService reviewService;


    @GetMapping("/{productSlug}")
    public ResponseEntity<ProductDetailResponse> getProductBySlug(@PathVariable String productSlug) {
        ProductDetailResponse product = productService.getActiveBySlug(productSlug);
        return ResponseEntity.ok(product);
    }

    @GetMapping("/{productSlug}/colors")
    public ResponseEntity<List<ColorResponse>> getColors(
            @PathVariable String productSlug) {
        List<ColorResponse> products = colorService.findActiveColorsByProductSlug(productSlug);
        return ResponseEntity.ok(products);
    }


    @GetMapping
        public ResponseEntity<Slice<ProductListItemResponse>> search(@ModelAttribute PublicProductSearchCondition condition, Pageable pageable){
        Slice<ProductListItemResponse> products = productService.search(condition,pageable);
        return ResponseEntity.ok(products);
    }

    @GetMapping("{productSlug}/reviews")
    public Page<ProductReviewResponse> getReviewsByProductSlug(
            @PathVariable String productSlug, Pageable pageable) {
        return reviewService.getReviewsByProductSlug(productSlug, pageable);
    }

}
