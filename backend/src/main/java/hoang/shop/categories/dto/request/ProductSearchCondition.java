package hoang.shop.categories.dto.request;

import hoang.shop.common.enums.ProductStatus;

import java.math.BigDecimal;

public record ProductSearchCondition(
        Long categoryId,
        Long brandId,
        Long tagId,
        ProductStatus status,
        String keyword,
        BigDecimal minPrice,
        BigDecimal maxPrice
) {
}
