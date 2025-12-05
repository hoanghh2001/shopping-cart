package hoang.shop.categories.dto.request;

import jakarta.validation.constraints.NotBlank;

public record CategoryCreateRequest (
        @NotBlank
        String name,
        @NotBlank
        String slug,
        Long parentId,

        String imageUrl
){
}
