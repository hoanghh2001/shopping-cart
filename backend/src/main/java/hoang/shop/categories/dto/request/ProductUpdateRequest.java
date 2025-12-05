package hoang.shop.categories.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ProductUpdateRequest(
        Long brandId,

        @NotNull
        Long categoryId,

        @NotBlank
        String name,

        @NotBlank
        String slug,

        String description

) {
}
