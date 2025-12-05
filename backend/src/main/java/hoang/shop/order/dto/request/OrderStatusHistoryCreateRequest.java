package hoang.shop.order.dto.request;

import jakarta.validation.constraints.NotNull;
import hoang.shop.common.enums.OrderStatus;

public record OrderStatusHistoryCreateRequest(
        @NotNull Long orderId,
        OrderStatus newStatus

) {
}
