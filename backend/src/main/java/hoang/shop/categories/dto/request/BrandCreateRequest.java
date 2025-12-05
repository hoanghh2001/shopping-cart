package hoang.shop.categories.dto.request;

import jakarta.validation.constraints.NotBlank;

public record BrandCreateRequest(
        @NotBlank
        String name,
        @NotBlank
        String slug,
        String description,
        String logoUrl
) {
}
