package hoang.shop.order.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record OrderItemCreateRequest(
        @NotNull(message = "{error.product-variant.id.not-null}")
        Long productVariantId,

        String sku,

        @NotNull(message = "{error.order-item.quantity.not-null}")
        @Min(value = 1,message = "{error.order-item.quantity.min}")
        Integer quantity

) {
}
