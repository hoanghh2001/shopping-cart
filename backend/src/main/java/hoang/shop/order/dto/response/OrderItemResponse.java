package hoang.shop.order.dto.response;

import java.math.BigDecimal;

public record OrderItemResponse (
        Long id,
        Long productVariantId,
        String productName,
        String productVariantName,
        String sku,
        Integer quantity,
        BigDecimal unitPriceBefore,
        BigDecimal unitPriceAtOrder,
        BigDecimal lineDiscount,
        BigDecimal lineTotal,
        String imageUrl
){
}
