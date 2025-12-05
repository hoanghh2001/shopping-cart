package hoang.shop.categories.dto.request;

import org.hibernate.internal.util.StringHelper;

public record ProductImageCreateRequest (
        Long productId,
        String imageUrl,
        String altText,
        Boolean isMain,
        Integer width,
        Integer heigh,
        String mimeType,
        Long size_bytes,
        String checksum
){
}
