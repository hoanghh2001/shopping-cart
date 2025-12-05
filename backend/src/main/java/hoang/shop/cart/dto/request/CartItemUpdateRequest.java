package hoang.shop.cart.dto.request;

import jakarta.validation.constraints.Min;

import java.math.BigDecimal;

public record CartItemUpdateRequest (
        @Min(value = 1, message = "{error.cart-item.quantity.minPrice}")
        Integer quantity
){
}
