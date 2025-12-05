package hoang.shop.order.dto.response;

import java.math.BigDecimal;

public record OrderItemResponse (
        Long id,
        Long productVariantId,
        String sizeName,
        String colorName,
        String colorHex,
        String sku,
        Integer quantity,
        BigDecimal unitPriceBefore,
        BigDecimal unitPriceAtOrder,
        BigDecimal lineTotal,
        String imageUrl
){
}
