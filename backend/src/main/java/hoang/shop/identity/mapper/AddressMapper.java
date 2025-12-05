package hoang.shop.identity.mapper;

import hoang.shop.config.MapStructConfig;
import hoang.shop.identity.dto.request.AddressCreateRequest;
import hoang.shop.identity.dto.request.AddressUpdateRequest;
import hoang.shop.identity.dto.response.AddressResponse;
import hoang.shop.identity.model.Address;
import org.mapstruct.*;

@Mapper(componentModel = "spring",config = MapStructConfig.class)
public interface AddressMapper {
    Address toEntity(AddressCreateRequest dto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromDto(AddressUpdateRequest dto, @MappingTarget Address entity);
    @Mapping(source = "default", target = "isDefault")
    AddressResponse toResponse(Address entity);
}
