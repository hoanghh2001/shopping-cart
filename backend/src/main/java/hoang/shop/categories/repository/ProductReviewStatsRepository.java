package hoang.shop.categories.repository;

import hoang.shop.categories.model.ProductReviewStats;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProductReviewStatsRepository extends JpaRepository<ProductReviewStats,Long> {

    Optional<ProductReviewStats> findByProductId(Long productId);
}
