package hoang.shop.order.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record OrderCreateRequest(
        @NotNull
        Long userId,
        @NotNull
        Long addressId,
        String note,
        @NotEmpty
        List<OrderItemCreateRequest> items
) {
}
