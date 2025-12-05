package hoang.shop.categories.dto.response;

import java.time.Instant;

public record ProductReviewResponse (
        Long id,
        int rating,
        String title,
        String content,
        String imageUrl,
        Long userId,
        String userName,
        String userAvatarUrl,
        Instant createdAt,
        Instant updatedAt
){
}
