package hoang.shop.cart.dto.request;

import hoang.shop.common.enums.PaymentMethod;

public record CheckoutRequest(
        String name,
        Long addressId,
        PaymentMethod paymentMethod,
        String note
) {
}
