package hoang.shop.categories.dto.response;

import java.util.List;

public record ProductColorDetailResponse (
        Long id,
        String name,
        String hex,
        boolean isDefault,
        List<ProductColorImageResponse> images,
        List<ProductVariantResponse> variants
){
}
