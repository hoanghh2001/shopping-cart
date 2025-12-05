package hoang.shop.cart.dto.request;

import hoang.shop.common.enums.CartStatus;

public record CartUpdateRequest(
        CartStatus status
) {
}
