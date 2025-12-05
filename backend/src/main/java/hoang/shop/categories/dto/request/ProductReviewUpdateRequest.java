package hoang.shop.categories.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

public record ProductReviewUpdateRequest(
        @Min(value = 1,message = "{error.review.rating.min}")
        @Max(value = 5,message = "{error.review.rating.max}")
        int rating,

        @Size(max = 2000)
        String content
) {
}
