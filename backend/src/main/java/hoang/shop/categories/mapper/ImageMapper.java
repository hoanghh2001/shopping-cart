package hoang.shop.categories.mapper;

import hoang.shop.categories.dto.request.ProductColorImageCreateRequest;
import hoang.shop.categories.dto.request.ProductColorImageUpdateRequest;
import hoang.shop.categories.dto.response.ProductColorImageResponse;
import hoang.shop.categories.model.ProductColorImage;
import hoang.shop.config.MapStructConfig;
import org.mapstruct.*;

@Mapper(componentModel = "spring", config = MapStructConfig.class)
public interface ProductColorImageMapper {
    ProductColorImage toEntity(ProductColorImageCreateRequest createRequest);
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void merge(ProductColorImageUpdateRequest updateRequest, @MappingTarget ProductColorImage image);
    ProductColorImageResponse toResponse(ProductColorImage productImage);
}
