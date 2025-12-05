package hoang.shop.cart.dto.request;


import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record CartItemCreateRequest(
        @NotNull(message = "{error.cart-item.product-variant-id.required}")
        Long variantId,
        @Min(value = 1, message = "{error.cart-item.quantity.minPrice}")
        Integer quantity
) {

}
