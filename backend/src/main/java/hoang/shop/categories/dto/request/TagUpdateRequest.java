package hoang.shop.categories.dto.request;

import hoang.shop.common.enums.status.TagStatus;

public record TagUpdateRequest(
        String name,
        String slug,
        String description,
        TagStatus status
) {
}
