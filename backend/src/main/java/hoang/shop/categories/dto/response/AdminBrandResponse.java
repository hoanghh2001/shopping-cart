package hoang.shop.categories.dto.response;

import hoang.shop.common.enums.BrandStatus;

import java.time.Instant;

public record AdminBrandDetailResponse(
        Long id,
        String name,
        String slug,
        String logoUrl,
        BrandStatus status,
        Instant createdAt,
        Instant updatedAt,
        Instant deletedAt

) {
}
