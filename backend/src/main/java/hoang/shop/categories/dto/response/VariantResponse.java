package hoang.shop.categories.dto.response;

import hoang.shop.common.enums.ProductVariantStatus;

public record ProductVariantResponse(
        Long id,
        String size,
        Integer stock,
        ProductVariantStatus status,
        boolean isDefault

) {
}
