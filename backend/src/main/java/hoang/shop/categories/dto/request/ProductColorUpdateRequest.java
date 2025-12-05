package hoang.shop.categories.dto.request;

import java.math.BigDecimal;

public record ProductColorUpdateRequest(
        String name,
        String hex
) {
}
