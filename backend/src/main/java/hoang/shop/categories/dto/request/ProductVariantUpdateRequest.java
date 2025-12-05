package hoang.shop.categories.dto.request;

import java.math.BigDecimal;

public record ProductVariantUpdateRequest(
        String variantName,
        String  color,
        String size,
        BigDecimal price,
        BigDecimal compareAtPrice,
        Integer stockQuantity
) {
}
