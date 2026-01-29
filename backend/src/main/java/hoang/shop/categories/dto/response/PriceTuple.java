package hoang.shop.categories.dto.response;

import java.math.BigDecimal;

public record PriceTuple(
        Long colorId,
        BigDecimal regularPrice,
        BigDecimal salePrice
) {
}
