package hoang.shop.categories.dto.response;

import java.util.List;

public record ColorDetailResponse(
        Long id,
        String name,
        String hex,
        boolean isDefault,
        List<ImageResponse> images,
        List<VariantResponse> variants
){
}
