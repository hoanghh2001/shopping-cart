package hoang.shop.categories.dto.request;

import hoang.shop.common.enums.CategoryStatus;
import jakarta.validation.constraints.NotBlank;

public record CategoryUpdateRequest(
        @NotBlank
        String name,
        @NotBlank
        String slug,
        Long parentId,
        String imageUrl,
        CategoryStatus status
) {
}
