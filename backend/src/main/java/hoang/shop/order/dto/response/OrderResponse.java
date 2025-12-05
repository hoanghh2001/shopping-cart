package hoang.shop.order.dto.response;

import hoang.shop.common.JapanAddress;
import hoang.shop.common.enums.status.OrderStatus;
import hoang.shop.common.enums.status.PaymentStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public record OrderResponse(
        Long id,
        String orderNumber,
        Long userId,
        JapanAddress shippingAddress,
        BigDecimal subtotalAmount,
        BigDecimal discountAmount,
        BigDecimal shippingFee,
        BigDecimal taxAmount,
        BigDecimal grandTotal,
        OrderStatus orderStatus,
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
