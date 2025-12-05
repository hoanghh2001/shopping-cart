package hoang.shop.categories.dto.response;

import hoang.shop.common.enums.BrandStatus;
import hoang.shop.common.enums.ProductStatus;

import java.time.Instant;
import java.util.List;

public record AdminProductResponse(
        Long id,
        String name,
        String slug,
        String description,
        ProductStatus status,

        Long brandId,
        String brandName,
        String brandSlug,

        Long categoryId,
        String categoryName,
        String categorySlug,

        String imageUrl,

        Instant createdAt,
        Long createdBy,
        Instant updatedAt,
        Long updateBy,
        Instant deletedAt,
        Long deletedBy
) {

}
