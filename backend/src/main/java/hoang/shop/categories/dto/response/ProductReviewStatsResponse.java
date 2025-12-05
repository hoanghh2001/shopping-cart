package hoang.shop.categories.dto.response;

public record ProductReviewStatsResponse (
        Double averageRating,
        Long reviewCount
){
}
