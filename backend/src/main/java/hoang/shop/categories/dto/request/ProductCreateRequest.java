package hoang.shop.categories.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record ProductCreateRequest (
        Long brandId,

        @NotNull
        Long categoryId,

        @NotBlank
        String name,

        String description

){
}
