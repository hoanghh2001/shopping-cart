package hoang.shop.identity.controller.user;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import hoang.shop.identity.dto.request.UserCreateRequest;
import hoang.shop.identity.dto.request.UserUpdateRequest;
import hoang.shop.identity.dto.response.UserResponse;
import hoang.shop.identity.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor

public class UserController {
    private final UserService userService;
    @PostMapping
    public ResponseEntity<UserResponse> createUser(@RequestBody @Valid UserCreateRequest userCreateRequest) {
        var saved = userService.createUser(userCreateRequest);
        URI location = URI.create("/api/users/"+saved.id());
        return ResponseEntity.created(location).body(saved);
    }
    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.findById(id));
    }
    @GetMapping("/by-phone/{phone}")
    public ResponseEntity<UserResponse> findByPhone(@PathVariable @Valid String phone) {
        return ResponseEntity.ok(userService.findByPhone(phone));
    }
    @GetMapping("/by-email/{email}")
    public ResponseEntity<UserResponse> findByEmail(@PathVariable @Valid String email) {
        return ResponseEntity.ok(userService.findByEmail(email));
    }
    @GetMapping("/active")
    public ResponseEntity<Page<UserResponse>> findActiveUsers(
            @PageableDefault(
                    page = 0,
                    size = 15,
                    sort = {"createdAt","id"},
                    direction = Sort.Direction.DESC
            ) Pageable pageable) {
        return ResponseEntity.ok(userService.listActiveUsers(pageable));
    }
    @GetMapping("/deleted")
    public ResponseEntity<Page<UserResponse>> findDeletedUsers(
            @PageableDefault(
                    page = 0,
                    size = 15,
                    sort = {"deletedAt","id"},
                    direction = Sort.Direction.DESC
            ) Pageable pageable){
        return ResponseEntity.ok(userService.listDeletedUsers(pageable));
    }
    @GetMapping("/search")
    public ResponseEntity<Page<UserResponse>> searchUsers(
            @RequestParam(defaultValue = "") String keyword,
            @PageableDefault(
                    page = 0,
                    size = 15,
                    sort = {"createdAt"},
                    direction = Sort.Direction.DESC
            ) Pageable pageable) {
        return ResponseEntity.ok(userService.searchUsers(keyword, pageable));
    }
    @GetMapping("/exists-email")
    public ResponseEntity<Boolean> existsByEmail(@PathVariable String email) {
        return ResponseEntity.ok(userService.existsByEmail(email));
    }
    @GetMapping("/exists-phone")
    public ResponseEntity<Boolean> existsByPhone(@PathVariable String phone) {
        return ResponseEntity.ok(userService.existsByPhone(phone));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserResponse> updateUser(@PathVariable Long id,@RequestBody @Valid UserUpdateRequest userUpdateRequest) {
        return ResponseEntity.ok(userService.updateUser(id, userUpdateRequest));
    }
    @DeleteMapping("{id}")
    public ResponseEntity<Void> softDeleteById(@PathVariable Long id) {
        userService.softDeleteById(id);
        return ResponseEntity.noContent().build();
    }
    @Autowired MessageSource ms;
    @GetMapping("/__i18n_probe")
    public Map<String,String> probe() {
        var key = "user.lastName.required";
        var ja = ms.getMessage(key, null, "MISSING:"+key, java.util.Locale.JAPAN);
        return Map.of("ja", ja);
    }
}
