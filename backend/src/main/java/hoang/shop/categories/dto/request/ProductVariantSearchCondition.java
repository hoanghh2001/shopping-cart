package hoang.shop.categories.dto.request;

import hoang.shop.common.enums.ProductVariantStatus;

public record ProductVariantSearchCondition(
        Long productId,
        Long colorId,
        String size,
        Integer minStock,
        Integer maxStock,
        String keyword,
        ProductVariantStatus status

) {
}
