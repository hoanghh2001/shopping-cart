package hoang.shop.categories.repository;

import hoang.shop.categories.model.ProductReview;
import hoang.shop.common.enums.status.ProductReviewStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProductReviewRepository extends JpaRepository<ProductReview,Long> {
    boolean existsByProductIdAndUserIdAndStatus(Long productId, Long userId, ProductReviewStatus status);

    Page<ProductReview> findByProduct_Id(Long productId, Pageable pageable);

    Optional<ProductReview> findByProduct_IdAndUser_Id(Long id, Long userId);

    Optional<ProductReview> findByIdAndUser_IdAndProduct_IdAndStatus(Long reviewId, Long userId, Long id, ProductReviewStatus status);
}
