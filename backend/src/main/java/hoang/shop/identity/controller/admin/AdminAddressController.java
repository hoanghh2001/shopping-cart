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

import java.util.List;

@RestController
@RequestMapping("/api/admin/addresses")
@RequiredArgsConstructor
public class AdminAddressController {
    private final AddressService addressService;

    @GetMapping("/{addressId}")
    public AddressResponse getByIdAdmin(@PathVariable Long addressId) {
        return addressService.getById(addressId);
    }

    @GetMapping("/users/{userId}")
    public List<AddressResponse> listByUserAdmin(
            @PathVariable Long userId) {
        return addressService.listByUser(userId);
    }

    @DeleteMapping("/{addressId}")
    public ResponseEntity<Void> softDeleteAdmin(@PathVariable Long addressId) {
        boolean ok = addressService.softDelete(addressId);
        return ok ? ResponseEntity.noContent().build()
                : ResponseEntity.notFound().build();
    }

    @PatchMapping("/{addressId}/restore")
    public ResponseEntity<Void> restoreAdmin(@PathVariable Long addressId) {
        boolean ok = addressService.restore(addressId);
        return ok ? ResponseEntity.noContent().build()
                : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{addressId}/hard")
    public ResponseEntity<Void> hardDeleteAdmin(@PathVariable Long addressId) {
        boolean ok = addressService.hardDelete(addressId);
        return ok ? ResponseEntity.noContent().build()
                : ResponseEntity.notFound().build();
    }

}
