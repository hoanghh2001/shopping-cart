package hoang.shop.categories.dto.response;

import java.math.BigDecimal;
import java.time.Instant;

public record AdminProductVariantResponse(
        Long id,
        Long sku,
        String size,
        Integer stock,
        String imageUrl,
        BigDecimal originalPrice,
        BigDecimal discountPrice,
        String status,
        boolean isDefault,

        Instant createdAt,
        Long createdBy,
        Instant updatedAt,
        Long updateBy,
        Instant deletedAt,
        Long deletedBy
) {
}
