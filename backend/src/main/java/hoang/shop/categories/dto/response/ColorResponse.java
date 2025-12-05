package hoang.shop.categories.dto.response;

import hoang.shop.common.enums.ProductColorStatus;

import java.math.BigDecimal;
import java.util.List;

public record ProductColorResponse(
        Long id,
        String name,
        String hex,
        BigDecimal originalPrice,
        BigDecimal discountPrice,
        List<ProductColorImageResponse> images,
        List<ProductVariantResponse> variants,
        ProductColorStatus status,
        boolean isDefault
) {
}
