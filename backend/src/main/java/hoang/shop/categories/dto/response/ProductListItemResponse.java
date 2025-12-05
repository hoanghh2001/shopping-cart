package hoang.shop.categories.dto.response;

import java.math.BigDecimal;
public record ProductListItemResponse(

        Long defaultColorId,

        String name,
        String slug,

        String brandName,
        String brandSlug,
        String tagName,
        String tagSlug,

        BigDecimal minPrice,
        BigDecimal maxPrice,


        Double averageRating,
        Long reviewCount,

        String imageUrl,

        boolean inStock
) {
}
