package hoang.shop.categories.dto.response;

import hoang.shop.common.enums.CategoryStatus;

import java.util.List;

public record CategoryResponse(
        Long id,
        String name,
        String slug,
        String imageUrl

) {
}
