package hoang.shop.categories.dto.response;

import java.util.List;

public record CategoryDetailResponse(
        Long id,
        String name,
        String slug,
        CategoryResponse parent,
        String imageUrl

) {
}
