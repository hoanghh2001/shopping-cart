package hoang.shop.categories.mapper;

import hoang.shop.categories.dto.request.BrandCreateRequest;
import hoang.shop.categories.dto.request.BrandUpdateRequest;
import hoang.shop.categories.dto.response.AdminBrandResponse;
import hoang.shop.categories.dto.response.BrandResponse;
import hoang.shop.categories.model.Brand;
import hoang.shop.config.MapStructConfig;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
@Mapper(componentModel = "spring", config = MapStructConfig.class)
public interface BrandMapper {
    Brand toEntity(BrandCreateRequest brandCreateRequest);
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void merge(BrandUpdateRequest brandUpdateRequest,@MappingTarget Brand brand);

    BrandResponse toResponse(Brand brand);

    AdminBrandResponse toAdminResponse(Brand brand);
}
