package hoang.shop.cart.dto.response;

import java.math.BigDecimal;

public record ItemResponse(
        Long cartItemId,
        Long variantId,
        String nameLabel,
        String colorLabel,
        String hexLabel,
        String sizeLabel,
        Integer quantity,
        BigDecimal unitPriceBefore,
        BigDecimal unitPriceAtOrder,
        BigDecimal lineTotal,
        String imageUrl
) {
}
