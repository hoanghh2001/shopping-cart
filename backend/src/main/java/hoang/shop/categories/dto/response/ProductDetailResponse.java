package hoang.shop.categories.dto.response;

import java.util.List;

public record ProductDetailResponse(
        String name,
        String slug,

        Long brandId,
        String brandName,
        String brandSlug,

        Long categoryId,
        String categoryName,
        String categorySlug,

        Double averageRating,
        Long reviewCount,
        ProductRatingStatsResponse ratingStats,

        String description,
        List<CategoryResponse> categoryPath,
        List<ColorResponse> colors
) {
}
