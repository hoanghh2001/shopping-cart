package hoang.shop.identity.mapper;

import hoang.shop.config.MapStructConfig;
import hoang.shop.identity.dto.request.RegisterRequest;
import hoang.shop.identity.dto.request.UserUpdateRequest;
import hoang.shop.identity.dto.response.UserResponse;
import hoang.shop.identity.model.User;
import org.mapstruct.*;
@Mapper(componentModel = "spring",config = MapStructConfig.class)
public interface UserMapper {
    User toEntity(RegisterRequest dto);
    @BeanMapping(nullValuePropertyMappingStrategy  = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromDto(UserUpdateRequest dto , @MappingTarget User entity);

    UserResponse toResponse(User entity);

}
