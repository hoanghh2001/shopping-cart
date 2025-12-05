package hoang.shop.categories.dto.response;

import hoang.shop.common.enums.status.BrandStatus;

public record BrandResponse(
        Long id,
        String name,
        String slug,
        String description,
        String logoUrl,
        BrandStatus status
) {
}
