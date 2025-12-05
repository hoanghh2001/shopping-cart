package hoang.shop.categories.dto.request;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.util.List;

public record ProductVariantCreateRequest(
        @NotBlank
        @Pattern(regexp = "S|M|L|XL", message = "{error.product-variant.size.invalid}")
        String size,
        @NotNull
        BigDecimal regularPrice,
        @NotNull

        BigDecimal salePrice,
        @NotNull
        @Min(value = 0,message = "{error.product-variant.stock-quantity}")
        Integer stock
) {
}
