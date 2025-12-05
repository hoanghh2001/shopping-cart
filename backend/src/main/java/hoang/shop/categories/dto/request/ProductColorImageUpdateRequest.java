package hoang.shop.categories.dto.request;

public record ProductColorImageUpdateRequest(
        String altText,
        Integer sortOrder,
        Boolean main
) {
}
