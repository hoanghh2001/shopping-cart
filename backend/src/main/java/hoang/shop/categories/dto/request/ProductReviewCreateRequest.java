package hoang.shop.categories.dto.request;

import java.util.List;

public record ProductReviewCreateRequest(
        int rating,
        String title,
        String content
) {
}
