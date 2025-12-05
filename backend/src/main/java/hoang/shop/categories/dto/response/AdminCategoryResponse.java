package hoang.shop.categories.dto.response;

import hoang.shop.common.enums.CategoryStatus;

import java.time.Instant;

public record AdminCategoryResponse(
        Long id,
        String name,
        String slug,
        CategoryResponse parent,
        String imageUrl,
        CategoryStatus status,

        Instant createdAt,
        Long createdBy,
        Instant updatedAt,
        Long updatedBy,
        Instant deletedAt,
        Long deletedBy
) {
}
