package hoang.shop.categories.mapper;

import hoang.shop.categories.dto.request.ProductColorCreateRequest;
import hoang.shop.categories.dto.request.ProductColorImageCreateRequest;
import hoang.shop.categories.dto.request.ProductColorImageUpdateRequest;
import hoang.shop.categories.dto.request.ProductColorUpdateRequest;
import hoang.shop.categories.dto.response.ProductColorImageResponse;
import hoang.shop.categories.dto.response.ProductColorResponse;
import hoang.shop.categories.model.ProductColor;
import hoang.shop.categories.model.ProductColorImage;
import hoang.shop.config.MapStructConfig;
import org.mapstruct.*;


@Mapper(componentModel = "spring", config = MapStructConfig.class)
public interface ProductColorMapper {

    ProductColor toEntity(ProductColorCreateRequest createRequest);
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void merge(ProductColorUpdateRequest updateRequest, @MappingTarget ProductColor color);
    ProductColorResponse toResponse(ProductColor productColor);}
