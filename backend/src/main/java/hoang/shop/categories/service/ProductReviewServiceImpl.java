package hoang.shop.categories.service;

import hoang.shop.categories.dto.request.ProductReviewCreateRequest;
import hoang.shop.categories.dto.request.ProductReviewUpdateRequest;
import hoang.shop.categories.dto.response.ProductReviewResponse;
import hoang.shop.categories.mapper.ProductReviewMapper;
import hoang.shop.categories.model.Product;
import hoang.shop.categories.model.ProductReview;
import hoang.shop.categories.repository.ProductRepository;
import hoang.shop.categories.repository.ProductReviewRepository;
import hoang.shop.common.enums.ProductStatus;
import hoang.shop.common.enums.status.ProductReviewStatus;
import hoang.shop.common.exception.BadRequestException;
import hoang.shop.common.exception.NotFoundException;
import hoang.shop.common.storage.FileStorageService;
import hoang.shop.identity.model.User;
import hoang.shop.identity.repository.UserRepository;
import hoang.shop.identity.service.CurrentUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.Instant;

@Service
@Transactional
@RequiredArgsConstructor
public class ProductReviewServiceImpl implements ProductReviewService{

    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final ProductReviewRepository reviewRepository;
    private final ProductReviewStatsService statsService;
    private final ProductReviewMapper reviewMapper;
    private final CurrentUserService currentUserService;
    private final FileStorageService fileStorageService;

    @Override
    public ProductReviewResponse createReview(
            String productSlug,
            ProductReviewCreateRequest request,
            MultipartFile imageFile) {

        Long userId = currentUserService.getCurrentUserId();

        Product product = productRepository.findBySlugAndStatus(productSlug,ProductStatus.ACTIVE)
                .orElseThrow(() -> new NotFoundException("{error.product.slug.not-found}"));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("{error.user.id.not-found}"));
        if (reviewRepository.existsByProductIdAndUserIdAndStatus(product.getId(), userId, ProductReviewStatus.ACTIVE)) {
            throw new BadRequestException("{error.review.product-user.already-exists}");
        }

        ProductReview review = new ProductReview();
        review.setProduct(product);
        review.setUser(user);
        review.setRating(request.rating());
        review.setTitle(request.title());
        review.setContent(request.content());
        if (imageFile != null && !imageFile.isEmpty()) {
            String imageUrl = fileStorageService.saveReviewImage(userId, product.getId(), imageFile);
            review.setImageUrl(imageUrl);
        } else {
            review.setImageUrl(null);
        }

        reviewRepository.save(review);

        statsService.increaseRating(product.getId(), review.getRating());

        return reviewMapper.toResponse(review);
    }

    @Override
    public ProductReviewResponse updateReview(String productSlug,Long reviewId, ProductReviewUpdateRequest request) {
        Product product = productRepository.findBySlugAndStatus(productSlug,ProductStatus.ACTIVE)
                .orElseThrow(() -> new NotFoundException("{error.product.slug.not-found}"));

        Long userId = currentUserService.getCurrentUserId();

        ProductReview review = reviewRepository.findByIdAndUser_IdAndProduct_IdAndStatus(reviewId,userId,product.getId(),ProductReviewStatus.ACTIVE)
                .orElseThrow(() -> new NotFoundException("{error.review.id.not-found}"));

        int oldRating = review.getRating();

        int newRating = request.rating();

        reviewMapper.merge(request,review);
        if (oldRating != newRating){
            statsService.updateRating(
                    review.getProduct().getId(),
                    oldRating,
                    newRating
            );
        }

        reviewRepository.save(review);
        return reviewMapper.toResponse(review);
    }

    @Override
    public void deleteReview(String productSlug,Long reviewId) {

        Product product = productRepository.findBySlugAndStatus(productSlug,ProductStatus.ACTIVE)
                .orElseThrow(() -> new NotFoundException("{error.product.slug.not-found}"));

        Long userId = currentUserService.getCurrentUserId();

        ProductReview review = reviewRepository.findByIdAndUser_IdAndProduct_IdAndStatus(reviewId,userId,product.getId(),ProductReviewStatus.ACTIVE)
                .orElseThrow(() -> new NotFoundException("{error.review.id.not-found}"));
        int oldRating = review.getRating();
        statsService.decreaseRating(product.getId(),oldRating);
        review.setStatus(ProductReviewStatus.DELETED);
        review.setDeletedAt(Instant.now());
        review.setUpdatedAt(Instant.now());
        reviewRepository.save(review);
    }

    @Override
    public Page<ProductReviewResponse> getReviewsByProductSlug(String productSlug, Pageable pageable) {

        Product product = productRepository.findBySlugAndStatus(productSlug,ProductStatus.ACTIVE)
                .orElseThrow(() -> new NotFoundException("{error.product.slug.not-found}"));

        Page<ProductReview> reviews = reviewRepository.findByProduct_Id(product.getId(),pageable);
        return reviews.map(reviewMapper::toResponse);
    }

    @Override
    public ProductReviewResponse getMyReviewForProduct(String productSlug) {
        Long userId = currentUserService.getCurrentUserId();

        Product product = productRepository.findBySlugAndStatus(
                productSlug,
                ProductStatus.ACTIVE
        ).orElseThrow(() -> new NotFoundException("{error.product.slug.not-found}"));

        return reviewRepository.findByProduct_IdAndUser_Id(product.getId(), userId)
                .map(reviewMapper::toResponse)
                .orElse(null);
    }
}
