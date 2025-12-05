package hoang.shop.categories.dto.response;

import hoang.shop.common.enums.BrandStatus;

import java.time.Instant;

public record AdminBrandResponse(
        Long id,
        String name,
        String slug,
        String logoUrl,
        BrandStatus status,

        Instant createdAt,
        Long createdBy,
        Instant updatedAt,
        Long updatedBy,
        Instant deletedAt,
        Long deletedBy

) {
}
