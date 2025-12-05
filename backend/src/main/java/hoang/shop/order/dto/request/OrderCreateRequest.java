package hoang.shop.order.dto.request;

import hoang.shop.common.enums.PaymentMethod;

public record OrderCreateRequest(
        Long addressId,
        String note,
        PaymentMethod paymentMethod
) {
}
