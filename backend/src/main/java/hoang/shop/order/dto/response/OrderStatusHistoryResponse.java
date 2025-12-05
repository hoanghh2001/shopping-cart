package hoang.shop.order.dto.response;

import hoang.shop.common.enums.status.ChangedByType;
import hoang.shop.common.enums.status.OrderStatus;

import java.time.Instant;

public record   OrderStatusHistoryResponse(
        Long id,
        Long orderId,
        OrderStatus oldStatus,
        OrderStatus newStatus,
        ChangedByType changedByType,
        Long changedById,
        String note,
        Instant changedAt
) {
}
