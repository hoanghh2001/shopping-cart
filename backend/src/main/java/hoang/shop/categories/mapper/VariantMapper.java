package hoang.shop.categories.mapper;

import hoang.shop.categories.dto.request.ProductVariantCreateRequest;
import hoang.shop.categories.dto.request.ProductVariantUpdateRequest;
import hoang.shop.categories.dto.response.AdminVariantResponse;
import hoang.shop.categories.dto.response.VariantResponse;
import hoang.shop.categories.model.ProductVariant;
import hoang.shop.config.MapStructConfig;
import org.mapstruct.*;

@Mapper(componentModel = "spring", config = MapStructConfig.class)

public interface VariantMapper {
    @Mapping(target = "regularPrice",source = "variant.regularPrice")
    ProductVariant toEntity(ProductVariantCreateRequest variant);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void merger(ProductVariantUpdateRequest updateRequest,@MappingTarget ProductVariant productVariant);

    AdminVariantResponse toAdminResponse(ProductVariant productVariant);

    VariantResponse toResponse(ProductVariant productVariant);




}
