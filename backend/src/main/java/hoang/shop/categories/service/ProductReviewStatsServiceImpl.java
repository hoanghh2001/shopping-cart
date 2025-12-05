package hoang.shop.categories.service;

import hoang.shop.categories.model.ProductReviewStats;
import hoang.shop.categories.repository.ProductReviewStatsRepository;
import hoang.shop.common.exception.BadRequestException;
import hoang.shop.common.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;

@Service
@Transactional
@RequiredArgsConstructor
public class ProductReviewStatsServiceImpl implements ProductReviewStatsService{
    private final ProductReviewStatsRepository statsRepository;
    @Override
    public void increaseRating(Long productId, int rating) {
        ProductReviewStats stats = statsRepository.findById(productId)
                .orElseGet(() -> createEmptyStats(productId));
        stats.setReviewCount(stats.getReviewCount() + 1L);
        switch (rating) {
            case 1 -> stats.setRating1(stats.getRating1() + 1L);
            case 2 -> stats.setRating2(stats.getRating2() + 1L);
            case 3 -> stats.setRating3(stats.getRating3() + 1L);
            case 4 -> stats.setRating4(stats.getRating4() + 1L);
            case 5 -> stats.setRating5(stats.getRating5() + 1L);
            default -> throw new BadRequestException("{error.product-review-stats.bad-request}");
        }
        long totalScore =
                        stats.getRating1() +
                        stats.getRating2() * 2 +
                        stats.getRating3() * 3 +
                        stats.getRating4() * 4 +
                        stats.getRating5() * 5;

        BigDecimal totalScoreBD = BigDecimal.valueOf(totalScore);
        BigDecimal reviewCountBD = BigDecimal.valueOf(stats.getReviewCount());

        BigDecimal avg = totalScoreBD
                .divide(reviewCountBD, 2, RoundingMode.HALF_UP);
        stats.setAverageRating(avg);
        statsRepository.save(stats);

    }

    @Override
    public void updateRating(Long productId, int oldRating, int newRating) {
        if (oldRating == newRating) {
            return;
        }

        ProductReviewStats stats = statsRepository.findById(productId)
                .orElseThrow(() -> new NotFoundException("{error.product-review-stats.not-found}"));

        decreaseBucket(stats, oldRating);

        increaseBucket(stats, newRating);

        recalculateAverage(stats);

        stats.setUpdatedAt(Instant.now());
        statsRepository.save(stats);

    }

    @Override
    public void decreaseRating(Long productId, int rating) {
        ProductReviewStats stats = statsRepository.findById(productId)
                .orElseThrow(() -> new BadRequestException("{error.product-review-stats.bad-request}"));

        if (stats.getReviewCount() > 0) {
            stats.setReviewCount(stats.getReviewCount() - 1);
        }
        decreaseBucket(stats, rating);

        if (stats.getReviewCount() <= 0) {
            stats.setReviewCount(0);
            stats.setRating1(0);
            stats.setRating2(0);
            stats.setRating3(0);
            stats.setRating4(0);
            stats.setRating5(0);
            stats.setAverageRating(BigDecimal.valueOf(0));
        } else {
            recalculateAverage(stats);
        }
        stats.setUpdatedAt(Instant.now());
        statsRepository.save(stats);

    }
    private ProductReviewStats createEmptyStats(Long productId) {
        return ProductReviewStats.builder()
                .productId(productId)
                .reviewCount(0)
                .rating1(0)
                .rating2(0)
                .rating3(0)
                .rating4(0)
                .rating5(0)
                .build();
    }
    private void increaseBucket(ProductReviewStats stats, int rating) {
        switch (rating) {
            case 1 -> stats.setRating1(stats.getRating1() + 1L);
            case 2 -> stats.setRating2(stats.getRating2() + 1L);
            case 3 -> stats.setRating3(stats.getRating3() + 1L);
            case 4 -> stats.setRating4(stats.getRating4() + 1L);
            case 5 -> stats.setRating5(stats.getRating5() + 1L);
            default -> throw new BadRequestException("{error.product-review-stats.bad-request}");
        }
    }

    private void decreaseBucket(ProductReviewStats stats, int rating) {
        switch (rating) {
            case 1 -> stats.setRating1(Math.max(0, stats.getRating1() - 1L));
            case 2 -> stats.setRating2(Math.max(0, stats.getRating2() - 1L));
            case 3 -> stats.setRating3(Math.max(0, stats.getRating3() - 1L));
            case 4 -> stats.setRating4(Math.max(0, stats.getRating4() - 1L));
            case 5 -> stats.setRating5(Math.max(0, stats.getRating5() - 1L));
            default -> throw new  BadRequestException("{error.product-review-stats.bad-request}");
        }
    }
    private void recalculateAverage(ProductReviewStats stats) {
        long totalScore =
                stats.getRating1() +
                        stats.getRating2() * 2L +
                        stats.getRating3() * 3L +
                        stats.getRating4() * 4L +
                        stats.getRating5() * 5L;

        if (stats.getReviewCount() == 0) {
            stats.setAverageRating(BigDecimal.valueOf(0));
        } else {
            stats.setAverageRating(
                    BigDecimal.valueOf(totalScore).divide(BigDecimal.valueOf(stats.getReviewCount(),2),RoundingMode.HALF_UP));
        }
    }

}
