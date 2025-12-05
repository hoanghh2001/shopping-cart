package hoang.shop.categories.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record ProductCreateRequest (
        @NotBlank
        String name,
        String description,
        @NotBlank
        String slug,
        @NotNull
        BigDecimal price,
        @NotNull
        BigDecimal discountPrice,
        @NotNull
        Integer stockQuantity,
        Long brandId,
        Long categoryId

){
}
