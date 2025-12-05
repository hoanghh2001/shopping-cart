package hoang.shop.categories.dto.response;

import java.math.BigDecimal;
import java.time.Instant;

public record AdminVariantResponse(
        Long id,
        Long sku,
        String size,
        Integer stock,
        String imageUrl,
        BigDecimal regularPrice,
        BigDecimal salePrice,
        String status,

        Instant createdAt,
        Long createdBy,
        Instant updatedAt,
        Long updateBy,
        Instant deletedAt,
        Long deletedBy
) {
}
