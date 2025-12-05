package hoang.shop.categories.dto.response;

public record ImageResponse(
        Long id,
        String imageUrl,
        String altText,
        boolean main,
        Integer sortOrder
) {
}
