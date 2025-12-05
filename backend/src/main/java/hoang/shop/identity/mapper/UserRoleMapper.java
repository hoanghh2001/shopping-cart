package hoang.shop.identity.mapper;


import hoang.shop.identity.dto.response.UserRoleResponse;
import hoang.shop.identity.model.UserRole;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserRoleMapper {
    @Mapping(source = "user.id",target = "userId")
    @Mapping(source = "role.id",target = "roleId")
    UserRoleResponse toResponse(UserRole entity);
}
