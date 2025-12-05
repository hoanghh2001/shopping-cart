package hoang.shop.identity.service;

import hoang.shop.identity.dto.response.RoleResponse;
import hoang.shop.identity.dto.response.UserResponse;
import hoang.shop.identity.dto.response.UserRoleResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserRoleService{
    Page<UserRoleResponse> listUserRole(Pageable pageable);
    boolean assignRoleToUser(Long userId, Long roleId, Long actorId);
    boolean removeRoleFromUser(Long userId, Long roleId ,Long actorId);
    Page<RoleResponse> listRolesOfUser(Long userId, Pageable pageable);
    Page<UserResponse> listUserOfRole(Long roleId, Pageable pageable);
    boolean userHasRole(Long userId, Long roleId);

}
