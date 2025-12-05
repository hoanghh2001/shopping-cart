package hoang.shop.categories.dto.response;

import hoang.shop.common.enums.TagStatus;

public record TagResponse(
        Long id,
        String name,
        String slug,
        String description
) {
}
