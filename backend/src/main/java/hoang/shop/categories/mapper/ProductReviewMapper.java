package hoang.shop.categories.mapper;

import hoang.shop.categories.dto.request.BrandCreateRequest;
import hoang.shop.categories.dto.request.BrandUpdateRequest;
import hoang.shop.categories.dto.request.ProductReviewCreateRequest;
import hoang.shop.categories.dto.request.ProductReviewUpdateRequest;
import hoang.shop.categories.dto.response.BrandResponse;
import hoang.shop.categories.dto.response.ProductReviewResponse;
import hoang.shop.categories.model.Brand;
import hoang.shop.categories.model.ProductReview;
import hoang.shop.config.MapStructConfig;
import org.mapstruct.*;

@Mapper(componentModel = "spring", config = MapStructConfig.class)

public interface ProductReviewMapper {
    ProductReview toEntity(ProductReviewCreateRequest productReviewCreateRequest);
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void merge(ProductReviewUpdateRequest productReviewUpdateRequest, @MappingTarget ProductReview productReview);
    @Mapping(target = "userId",source = "review.user.id")
    @Mapping(target = "userName",source = "review.user.fullName")
    @Mapping(target = "userAvatarUrl",source = "review.user.avatarUrl")
    ProductReviewResponse toResponse(ProductReview review);

}
