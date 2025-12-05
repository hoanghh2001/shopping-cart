package hoang.shop.categories.dto.request;

import jakarta.validation.constraints.NotBlank;

public record CategoryUpdateRequest(
        @NotBlank
        String name,
        @NotBlank
        String slug,
        Long parentId,
        String imageUrl
) {
}
