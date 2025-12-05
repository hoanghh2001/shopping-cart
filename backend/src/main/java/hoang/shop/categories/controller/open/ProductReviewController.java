package hoang.shop.categories.controller.open;

import hoang.shop.categories.dto.request.ProductReviewCreateRequest;
import hoang.shop.categories.dto.request.ProductReviewUpdateRequest;
import hoang.shop.categories.dto.response.ProductReviewResponse;
import hoang.shop.categories.service.ProductReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/products/{productSlug}/my-reviews")
@RequiredArgsConstructor
public class ProductReviewController {
    ProductReviewService reviewService;

    @PostMapping
    public ProductReviewResponse createReview(
            @PathVariable String productSlug,
            @RequestBody ProductReviewCreateRequest request,
            @RequestPart(value = "imageFile", required = false) MultipartFile imageFile) {
        return reviewService.createReview(productSlug, request,imageFile);
    }

    @PutMapping("/{reviewId}")
    public ProductReviewResponse updateReview(
            @PathVariable String productSlug,
            @PathVariable Long reviewId,
            @RequestBody ProductReviewUpdateRequest request) {
        return reviewService.updateReview(productSlug,reviewId, request);
    }
    @PatchMapping("/{reviewId}/delete")
    public void deleteReview(
            @PathVariable String productSlug,
            @PathVariable Long reviewId) {
        reviewService.deleteReview(productSlug, reviewId);
    }

    @GetMapping
    public ProductReviewResponse getMyReviewForProduct(String productSlug) {
        return reviewService.getMyReviewForProduct(productSlug);
    }
}
