package hoang.shop.categories.mapper;

import hoang.shop.categories.dto.request.CategoryCreateRequest;
import hoang.shop.categories.dto.request.CategoryUpdateRequest;
import hoang.shop.categories.dto.response.CategoryResponse;
import hoang.shop.categories.model.Category;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")

public interface CategoryMapper {
    Category toEntity(CategoryCreateRequest createRequest);
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void merge(CategoryUpdateRequest updateRequest, @MappingTarget Category category);
    CategoryResponse toResponse(Category category);
}
