package hoang.shop.order.dto.response;

import hoang.shop.common.enums.OrderStatus;
import hoang.shop.common.enums.PaymentMethod;
import hoang.shop.common.enums.PaymentStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public record OrderResponse(
        OrderStatus orderStatus,
        Long id,
        Long userId,
        String name,
        String postalCode,
        String phone,
        String fullAddress,
        BigDecimal subtotalAmount,
        BigDecimal taxAmount,
        BigDecimal discountAmount,
        BigDecimal shippingFee,
        BigDecimal grandTotal,
        PaymentMethod paymentMethod,
        PaymentStatus paymentStatus,
        Instant placedAt,
        Instant paidAt,
        Instant shippedAt,
        Instant deliveredAt,
        Instant cancelledAt,
        String note,
        List<OrderItemResponse> items
) {
}
