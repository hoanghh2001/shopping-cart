package hoang.shop.cart.dto.request;

import java.math.BigDecimal;

public record CartItemUpdateRequest (
        Long cartId,
        Long productVariantId,
        Integer quantity
){
}
