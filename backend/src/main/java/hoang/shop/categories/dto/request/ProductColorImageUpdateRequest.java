package hoang.shop.categories.dto.request;

import hoang.shop.common.enums.status.ProductImageStatus;

public record ProductImageUpdateRequest(
        String imageUrl,
        String altText,
        Boolean isMain,
        Integer width,
        Integer heigh,
        String mimeType,
        Long size_bytes,
        String checksum,
        ProductImageStatus status
) {
}
