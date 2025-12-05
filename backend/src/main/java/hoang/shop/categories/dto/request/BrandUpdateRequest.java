package hoang.shop.categories.dto.request;

import jakarta.validation.constraints.NotBlank;

import java.time.Instant;

public record BrandUpdateRequest(
        @NotBlank
        String name,
        @NotBlank
        String slug,
        String description,
        String logoUrl
) {
}
