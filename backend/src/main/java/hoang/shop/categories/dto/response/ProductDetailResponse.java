package hoang.shop.categories.dto.response;

import hoang.shop.common.enums.status.ProductStatus;

import java.math.BigDecimal;
import java.util.List;

public record ProductDetailResponse(
        Long id,
        String name,
        String description,
        String slug,
        BigDecimal originalPrice,
        BigDecimal discountPrice,
        Integer stockQuantity,
        ProductStatus status,
        CategorySummaryResponse category,
        List<TagResponse> tags,
        List<ProductVariantResponse> productVariants,
        ProductImageResponse productImage

) {
}
