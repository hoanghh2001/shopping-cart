package hoang.shop.categories.dto.response;

public record AdminProductColorImageResponse(
        Long id,
        String imageUrl,
        String altText,
        Integer width,
        Integer height,
        boolean main,
        Integer sortOrder
) {
}
