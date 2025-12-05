package hoang.shop.categories.mapper;

import hoang.shop.categories.dto.request.ProductVariantCreateRequest;
import hoang.shop.categories.dto.request.ProductVariantUpdateRequest;
import hoang.shop.categories.dto.response.ProductVariantResponse;
import hoang.shop.categories.model.ProductVariant;
import hoang.shop.config.MapStructConfig;
import org.mapstruct.*;

@Mapper(componentModel = "spring", config = MapStructConfig.class)

public interface ProductVariantMapper {
    ProductVariant toEntity(ProductVariantCreateRequest createRequest);
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void merger(ProductVariantUpdateRequest updateRequest,@MappingTarget ProductVariant productVariant);
    ProductVariantResponse toResponse(ProductVariant productVariant);


}
