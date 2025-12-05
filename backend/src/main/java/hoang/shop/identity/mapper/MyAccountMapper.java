package hoang.shop.identity.mapper;

import hoang.shop.identity.dto.response.MyAccountResponse;
import hoang.shop.identity.model.Role;
import hoang.shop.identity.model.User;
import hoang.shop.identity.model.UserRole;
import hoang.shop.identity.security.UserPrincipal;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.List;

@Mapper(componentModel = "spring",unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface MyAccountMapper {


    @Mapping(target = "roles", expression = "java(mapRoles(user))")
    MyAccountResponse fromUser(User user);

    default List<String> mapRoles(User user) {
        if (user.getUserRoles() == null) return List.of();
        return user.getUserRoles().stream()
                .map(UserRole::getRole)
                .map(Role::getName)
                .map(name -> "ROLE_" + name)
                .toList();
    }

}
