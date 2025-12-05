package hoang.shop.cart.dto.request;

import hoang.shop.common.enums.status.CartStatus;

public record CartUpdateRequest(
        Long id,
        Long userId,
        String sessionId,
        Integer totalQuantity,
        CartStatus status
) {
}
