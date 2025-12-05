package hoang.shop.categories.dto.request;

import jakarta.validation.constraints.NotBlank;

public record CategoryCreateRequest (
        @NotBlank
        String name,
        Long parentId,
        String imageUrl
){
}
