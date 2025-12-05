package hoang.shop.order.dto.request;

import hoang.shop.common.enums.OrderStatus;
import hoang.shop.common.enums.PaymentStatus;

import java.math.BigDecimal;
import java.time.Instant;

public record OrderSearchCondition(
        Long userId,
        OrderStatus orderStatus,
        PaymentStatus paymentStatus,
        Instant fromDate,
        Instant toDate,
        BigDecimal minTotal,
        BigDecimal maxTotal,
        String keyword,
        String paymentMethod,
        String receiverName,
        String receiverPhone
) {
}
