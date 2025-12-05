package hoang.shop.categories.dto.response;

import hoang.shop.common.enums.BrandStatus;
import hoang.shop.common.enums.CategoryStatus;

import java.time.Instant;

public record AdminCategoryDetailResponse(
        Long id,
        String name,
        String slug,
        CategoryResponse parent,
        String imageUrl,
        CategoryStatus status,
        Instant createdAt,
        Instant updatedAt,
        Instant deletedAt
) {
}
