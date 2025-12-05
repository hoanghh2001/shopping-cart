package hoang.shop.categories.dto.response;

import hoang.shop.common.enums.status.TagStatus;

import java.util.Set;

public record TagResponse(
        Long id,
        String name,
        String slug,
        String description,
        TagStatus status,
        Set<ProductResponse> products
) {
}
