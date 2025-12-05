package hoang.shop.categories.dto.response;

import hoang.shop.common.enums.ProductColorStatus;

import java.math.BigDecimal;
import java.util.List;

public record ColorResponse(
        Long id,
        String name,
        String hex,

        List<ImageResponse> images,
        List<VariantResponse> variants,
        ProductColorStatus status,
        boolean isDefault
) {
}
