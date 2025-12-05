package hoang.shop.categories.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.math.BigDecimal;

public record ProductVariantUpdateRequest(
        @Pattern(regexp = "S|M|L|XL", message = "{error.product-variant.size.invalid}")
        String size,
        BigDecimal regularPrice,
        BigDecimal salePrice,
        @Min(value = 0,message = "{error.product-variant.stock-quantity}")
        Integer stockQuantity
) {
}
