package hoang.shop.categories.dto.response;

public record ProductColorImageResponse(
        Long id,
        String imageUrl,
        String altText,
        boolean main,
        Integer sortOrder
) {
}
