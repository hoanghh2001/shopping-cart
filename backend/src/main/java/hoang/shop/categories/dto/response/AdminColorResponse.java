package hoang.shop.categories.dto.response;

import hoang.shop.common.enums.ProductColorStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public record AdminColorResponse(

        Long id,
        Long productId,
        String name,
        String hex,
        ProductColorStatus status,
        boolean isDefault,
        Instant createdAt,
        Long createdBy,
        Instant updatedAt,
        Long updatedBy,
        Instant deletedAt,
        Long deletedBy
) {
}
