package hoang.shop.identity.controller.admin;

import hoang.shop.identity.dto.request.RoleCreateRequest;
import hoang.shop.identity.dto.request.RoleUpdateRequest;
import hoang.shop.identity.dto.response.RoleResponse;
import hoang.shop.identity.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
@RestController
@RequestMapping("/api/admin/roles")
@RequiredArgsConstructor
public class AdminRoleController {
    private final RoleService roleService;
    @PostMapping
    public ResponseEntity<RoleResponse> create(@RequestBody RoleCreateRequest roleCreateRequest) {
        return ResponseEntity.ok(roleService.create(roleCreateRequest));
    }
    @GetMapping("/id/{id}")
    public ResponseEntity<RoleResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(roleService.findById(id));
    }
    @GetMapping("/name/{name}")
    public ResponseEntity<RoleResponse> findByName(@PathVariable String name) {
        return ResponseEntity.ok(roleService.findByName(name));
    }
    @GetMapping("/active")
    public ResponseEntity<Page<RoleResponse>> findAllActive(Pageable pageable) {
        return ResponseEntity.ok(roleService.findAllActive(pageable));
    }
    @GetMapping("/deleted")
    public ResponseEntity<Page<RoleResponse>> findAllDeleted(Pageable pageable) {
        return ResponseEntity.ok(roleService.findAllDeleted(pageable));
    }
    @GetMapping("/keyword/{keyword}")
    public ResponseEntity<Page<RoleResponse>> search(@PathVariable String keyword, Pageable pageable) {
        return ResponseEntity.ok(roleService.search(keyword, pageable));
    }
    @PatchMapping("/update/{id}")
    public ResponseEntity<RoleResponse> update(@PathVariable Long id, @RequestBody RoleUpdateRequest roleUpdateRequest) {
        return ResponseEntity.ok(roleService.update(id, roleUpdateRequest));
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Boolean> softDeleteById(@PathVariable Long id) {
        return ResponseEntity.ok(roleService.softDeleteById(id));
    }
    @PutMapping("/restore/{id}")
    public ResponseEntity<Boolean> restoreById(@PathVariable Long id) {
        return ResponseEntity.ok(roleService.restoreById(id));
    }
    @GetMapping("/exists/name/{name}")
    public ResponseEntity<Boolean> existsByName(@PathVariable String name) {
        return ResponseEntity.ok(roleService.existsByName(name));
    }
}

