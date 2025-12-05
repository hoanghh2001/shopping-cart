package hoang.shop.categories.dto.response;

import hoang.shop.common.enums.ProductVariantStatus;

import java.math.BigDecimal;

public record VariantResponse(
        Long id,
        String size,
        Integer stock,
        BigDecimal regularPrice,
        BigDecimal salePrice,
        ProductVariantStatus status

) {
}
