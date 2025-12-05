package hoang.shop.identity.controller.user;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import hoang.shop.identity.dto.request.AddressCreateRequest;
import hoang.shop.identity.dto.request.AddressUpdateRequest;
import hoang.shop.identity.dto.response.AddressResponse;
import hoang.shop.identity.service.AddressService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users/{userId}")
@RequiredArgsConstructor
public class MyProfileController {
    private final AddressService addressService;

    @GetMapping("/addresses/{addressId}")
    public AddressResponse getById(
            @PathVariable Long userId,
            @PathVariable Long addressId
    ) {
        return addressService.getById(userId, addressId);
    }

    @PutMapping("/addresses/{addressId}")
    public AddressResponse update(
            @PathVariable Long userId,
            @PathVariable Long addressId,
            @RequestBody @Valid AddressUpdateRequest request
    ) {
        return addressService.update(userId, addressId, request);
    }

    @DeleteMapping("/addresses/{addressId}")
    public ResponseEntity<Void> softDelete(
            @PathVariable Long userId,
            @PathVariable Long addressId
    ) {
        boolean ok = addressService.softDelete(userId, addressId);
        return ok ? ResponseEntity.noContent().build()
                : ResponseEntity.notFound().build();
    }

    @PatchMapping("/addresses/{addressId}/restore")
    public ResponseEntity<Void> restore(
            @PathVariable Long userId,
            @PathVariable Long addressId
    ) {
        boolean ok = addressService.restore(userId, addressId);
        return ok ? ResponseEntity.noContent().build()
                : ResponseEntity.notFound().build();
    }

    @GetMapping("/addresses")
    public Page<AddressResponse> list(
            @PathVariable Long userId,
            @PageableDefault(
                    page = 0,
                    size = 15,
                    sort = {"id", "createdAt"},
                    direction = Sort.Direction.DESC
            ) Pageable pageable
    ) {
        return addressService.list(userId, pageable);
    }

    @PutMapping("/addresses/{addressId}/default")
    public ResponseEntity<Void> setDefault(
            @PathVariable Long userId,
            @PathVariable Long addressId
    ) {
        boolean updated = addressService.setDefault(userId, addressId);
        return updated ? ResponseEntity.noContent().build()
                : ResponseEntity.notFound().build();
    }

    @GetMapping("/addresses/default")
    public ResponseEntity<AddressResponse> getDefault(@PathVariable Long userId) {
        AddressResponse res = addressService.getDefault(userId);
        return res != null ? ResponseEntity.ok(res)
                : ResponseEntity.noContent().build();
    }
}
