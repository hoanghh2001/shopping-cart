package hoang.shop.categories.service;

public interface ProductReviewStatsService {

    void increaseRating(Long productId, int rating);
    void updateRating(Long productId, int oldRating, int newRating);
    void decreaseRating(Long productId, int rating);
}
