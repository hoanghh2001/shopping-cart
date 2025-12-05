package hoang.shop.cart.dto.response;

import hoang.shop.common.enums.status.CartStatus;

import java.util.List;

public record CartResponse(
        Long id,
        Long userId,
        String sessionId,
        List<CartItemResponse> cartItems,
        Integer totalQuantity,
        CartStatus status
) {
}
