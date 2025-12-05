package hoang.shop.categories.dto.request;

import java.math.BigDecimal;

public record ProductVariantCreateRequest(
        String name,
        String  color,
        String size,
        BigDecimal price,
        BigDecimal compareAtPrice,
        Integer stockQuantity
) {
}
