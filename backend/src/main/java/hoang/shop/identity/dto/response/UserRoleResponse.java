package hoang.shop.identity.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.Instant;
@JsonInclude(JsonInclude.Include.NON_NULL)

public record UserRoleResponse (
        Long userId,
        Long roleId,
        Long assignedBy,
        Instant assignedAt,
        Long removedBy,
        Instant removedAt,
        boolean deleted,
        RoleResponse role,
        UserResponse user
){
}
