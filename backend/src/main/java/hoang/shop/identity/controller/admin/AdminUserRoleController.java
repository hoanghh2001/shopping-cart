package hoang.shop.identity.controller.admin;

import hoang.shop.identity.dto.response.RoleResponse;
import hoang.shop.identity.dto.response.UserResponse;
import hoang.shop.identity.dto.response.UserRoleResponse;
import hoang.shop.identity.service.UserRoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;


@RestController
@RequestMapping("/api/users/{userId}/roles")
@RequiredArgsConstructor
public class AdminUserRoleController {
    private final UserRoleService userRoleService;

    @PostMapping("/{roleId}")
    public ResponseEntity<Boolean> assignRoleToUser(
            @PathVariable Long userId,
            @PathVariable Long roleId,
            @RequestHeader("X-Actor-Id") Long actorId) {
        boolean created = userRoleService.assignRoleToUser(userId,roleId,actorId);
        if (created){
            return ResponseEntity
                    .created(URI.create("/api/users/%d/roles/%d".formatted(userId, roleId))).build();
        }
        return ResponseEntity.status(HttpStatus.CONFLICT).build();
    }
    @DeleteMapping("/{roleId}")
    public ResponseEntity<Void> removeRoleFromUser(
            @PathVariable Long userId,
            @PathVariable Long roleId,
            @RequestHeader("X-Actor-Id") Long actorId) {
        boolean removed = userRoleService.removeRoleFromUser(userId, roleId, actorId);
        return removed ? ResponseEntity.noContent().build()
                : ResponseEntity.notFound().build();
    }

    @GetMapping("/roles/{roleId}/is")
    public ResponseEntity<Boolean> userHasRole(
            @PathVariable Long userId,
            @PathVariable Long roleId) {
        boolean hasRole = userRoleService.userHasRole(userId, roleId);
        return ResponseEntity.ok(hasRole);
    }
    @GetMapping
    public ResponseEntity<Page<UserRoleResponse>> listUserRole(
            @PageableDefault(
                    page = 0,size = 20,sort = {"assignedAt","userId"},
                    direction = Sort.Direction.DESC)
            Pageable pageable){
        Page<UserRoleResponse> userRoleResponses = userRoleService.listUserRole(pageable);
        return ResponseEntity.ok(userRoleResponses);
    }
}

