package hoang.shop.identity.controller.admin;

import hoang.shop.identity.dto.response.AddressResponse;
import hoang.shop.identity.service.AddressService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AddressController {
    private final AddressService addressService;

    @GetMapping("/address/{addressId}")
    public AddressResponse getByIdAdmin(@PathVariable Long addressId) {
        return addressService.getById(addressId);
    }

    @GetMapping("//users/{userId}/addresses")
    public Page<AddressResponse> listByUserAdmin(
            @PathVariable Long userId,
            @PageableDefault(
                    page = 0,
                    size = 15,
                    sort = {"id", "createdAt"},
                    direction = Sort.Direction.DESC
            ) Pageable pageable
    ) {
        return addressService.listByUser(userId, pageable);
    }

    @DeleteMapping("/addresses/{addressId}")
    public ResponseEntity<Void> softDeleteAdmin(@PathVariable Long addressId) {
        boolean ok = addressService.softDelete(addressId);
        return ok ? ResponseEntity.noContent().build()
                : ResponseEntity.notFound().build();
    }

    @PatchMapping("/addresses/{addressId}/restore")
    public ResponseEntity<Void> restoreAdmin(@PathVariable Long addressId) {
        boolean ok = addressService.restore(addressId);
        return ok ? ResponseEntity.noContent().build()
                : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/addresses/{addressId}/hard")
    public ResponseEntity<Void> hardDeleteAdmin(@PathVariable Long addressId) {
        boolean ok = addressService.hardDelete(addressId);
        return ok ? ResponseEntity.noContent().build()
                : ResponseEntity.notFound().build();
    }

}
