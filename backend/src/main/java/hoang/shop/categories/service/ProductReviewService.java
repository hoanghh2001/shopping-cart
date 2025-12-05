package hoang.shop.categories.service;

import hoang.shop.categories.dto.request.ProductReviewCreateRequest;
import hoang.shop.categories.dto.request.ProductReviewUpdateRequest;
import hoang.shop.categories.dto.response.ProductReviewResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

public interface ProductReviewService {

    ProductReviewResponse createReview(String productSlug, ProductReviewCreateRequest request, MultipartFile imageFile);

    ProductReviewResponse updateReview(String productSlug,Long reviewId, ProductReviewUpdateRequest request);

    void deleteReview(String productSlug,Long reviewId);

    Page<ProductReviewResponse> getReviewsByProductSlug(String productSlug,
                                                        Pageable pageable);

    ProductReviewResponse getMyReviewForProduct(String productSlug);
}
