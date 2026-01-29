package hoang.shop.categories.dto.response;

import java.math.BigDecimal;
import java.time.Instant;

public record ProductListItemResponse(
        Long id,
        Long colorId,

        String name,
        String slug,

        String brandName,
        String brandSlug,

        BigDecimal regularPrice,
        BigDecimal salePrice,


        Double averageRating,
        Long reviewCount,

        String imageUrl,
        Instant createdAt,
        boolean inStock
) {
}
