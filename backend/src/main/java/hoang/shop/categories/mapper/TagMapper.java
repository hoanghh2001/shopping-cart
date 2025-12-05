package hoang.shop.categories.mapper;

import hoang.shop.categories.dto.request.TagCreateRequest;
import hoang.shop.categories.dto.request.TagUpdateRequest;
import hoang.shop.categories.dto.response.TagResponse;
import hoang.shop.categories.model.Tag;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")

public interface TagMapper {
    Tag toEntity(TagCreateRequest tagCreateRequest);
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void merge(TagUpdateRequest tagUpdateRequest,@MappingTarget Tag tag);
    TagResponse toResponse(Tag tag);
}
