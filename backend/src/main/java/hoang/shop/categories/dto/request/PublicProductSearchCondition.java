package hoang.shop.categories.dto.request;

import java.math.BigDecimal;

public record PublicProductSearchCondition(
        Long categoryId,
        Long brandId,
        Long tagId,
        String keyword,
        BigDecimal minPrice,
        BigDecimal maxPrice,
        String sort
) {

}
