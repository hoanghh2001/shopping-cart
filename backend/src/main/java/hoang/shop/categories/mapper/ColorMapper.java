package hoang.shop.categories.mapper;

import hoang.shop.categories.dto.request.ProductColorCreateRequest;
import hoang.shop.categories.dto.request.ProductColorUpdateRequest;
import hoang.shop.categories.dto.response.AdminColorResponse;
import hoang.shop.categories.dto.response.ColorDetailResponse;
import hoang.shop.categories.dto.response.ColorResponse;
import hoang.shop.categories.model.ProductColor;
import hoang.shop.config.MapStructConfig;
import org.mapstruct.*;


@Mapper(componentModel = "spring", config = MapStructConfig.class)
public interface ColorMapper {

    ProductColor toEntity(ProductColorCreateRequest createRequest);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void merge(ProductColorUpdateRequest updateRequest, @MappingTarget ProductColor color);

    ColorDetailResponse toDetailResponse(ProductColor productColor);

    ColorResponse toResponse(ProductColor productColor);

    AdminColorResponse toAdminResponse(ProductColor productColor);


}
