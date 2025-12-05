package hoang.shop.categories.mapper;

import hoang.shop.categories.dto.request.ProductCreateRequest;
import hoang.shop.categories.dto.request.ProductUpdateRequest;
import hoang.shop.categories.dto.response.ProductResponse;
import hoang.shop.categories.model.Product;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")

public interface ProductMapper {
    Product toEntity(ProductCreateRequest createRequest);
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void merge(ProductUpdateRequest updateRequest,@MappingTarget Product product);
    ProductResponse toResponse(Product product);

}
