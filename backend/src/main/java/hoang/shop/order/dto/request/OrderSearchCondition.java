package hoang.shop.order.dto.request;

import hoang.shop.common.enums.status.OrderStatus;
import hoang.shop.common.enums.status.PaymentStatus;

import java.math.BigDecimal;
import java.time.LocalDate;

public record OrderSearchCondition(
        Long userId,
        OrderStatus orderStatus,
        PaymentStatus paymentStatus,
        LocalDate fromDate,
        LocalDate toDate,
        BigDecimal minTotal,
        BigDecimal maxTotal,
        String keyword,
        String paymentMethod,
        String receiverName,
        String receiverPhone
) {
}
