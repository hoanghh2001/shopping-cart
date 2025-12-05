package hoang.shop.cart.dto.response;

import hoang.shop.common.enums.CartStatus;

import java.math.BigDecimal;
import java.util.List;

public record CartResponse(
        Long id,
        List<ItemResponse> cartItems,
        Integer totalQuantity,
        BigDecimal totalAmount
) {
}
