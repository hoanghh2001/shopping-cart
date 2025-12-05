package hoang.shop.cart.dto.response;

import java.math.BigDecimal;

public record CartItemResponse(
        Long id,
        Long productVariantId,
        String productVariantName,
        String productVariantColor,
        String productVariantSize,
        Integer quantity,
        BigDecimal unitPriceBefore,
        BigDecimal unitPrice,
        BigDecimal lineTotal,
        String imageUrl
) {
}
