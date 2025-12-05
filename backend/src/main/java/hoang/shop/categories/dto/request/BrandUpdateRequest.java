package hoang.shop.categories.dto.request;

import hoang.shop.common.enums.BrandStatus;
import jakarta.validation.constraints.NotBlank;

import java.time.Instant;

public record BrandUpdateRequest(
        @NotBlank
        String name,
        @NotBlank
        String slug,
        String description,
        String logoUrl,
        BrandStatus status
) {
}
