package hoang.shop.categories.dto.request;

public record TagCreateRequest(
        String name,
        String slug,
        String description
) {
}
