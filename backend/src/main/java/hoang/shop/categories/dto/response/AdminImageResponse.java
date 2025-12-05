package hoang.shop.categories.dto.response;

import hoang.shop.common.enums.ImageStatus;

import java.time.Instant;

public record AdminImageResponse(
        Long id,
        String imageUrl,
        String altText,
        Integer width,
        Integer height,
        boolean main,
        Integer sortOrder,
        ImageStatus status,

        Instant createdAt,
        Long createdBy,
        Instant updatedAt,
        Long updatedBy,
        Instant deletedAt,
        Long deletedBy
) {
}
