package hoang.shop.identity.controller.user;

import hoang.shop.identity.dto.request.AddressCreateRequest;
import hoang.shop.identity.dto.request.AddressUpdateRequest;
import hoang.shop.identity.dto.response.AddressResponse;
import hoang.shop.identity.service.AddressService;
import hoang.shop.identity.service.CurrentUserService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/my-account/addresses")
@RequiredArgsConstructor
@Validated
public class MyAddressController {
    private final AddressService addressService;
    private final CurrentUserService currentUserService;

    @PostMapping
    public ResponseEntity<AddressResponse> create(@Valid @RequestBody AddressCreateRequest request) {
        Long userId = currentUserService.getCurrentUserId();
        AddressResponse addressResponse = addressService.create(userId, request);
        URI location = URI.create("/api/my-account/addresses/"+addressResponse.id());
        return ResponseEntity.created(location).body(addressResponse);
    }
    @GetMapping("/{addressId}")
    public ResponseEntity<AddressResponse> getById(@Positive @Valid
            @PathVariable Long addressId
    ) {
        Long userId = currentUserService.getCurrentUserId();
        AddressResponse addressResponse = addressService.getById(userId, addressId);
        return ResponseEntity.ok(addressResponse);
    }

    @PutMapping("/{addressId}")
    public ResponseEntity<AddressResponse> update(
            @PathVariable Long addressId,
            @RequestBody @Valid AddressUpdateRequest request
    ) {
        Long userId = currentUserService.getCurrentUserId();
        AddressResponse addressResponse = addressService.update(userId, addressId, request);
        return ResponseEntity.ok(addressResponse);
    }

    @PatchMapping("/{addressId}/delete")
    public ResponseEntity<Void> softDelete(
            @PathVariable Long addressId
    ) {
        Long userId = currentUserService.getCurrentUserId();
        boolean ok = addressService.softDelete(userId, addressId);
        return ok ? ResponseEntity.noContent().build()
                : ResponseEntity.notFound().build();
    }

    @PatchMapping("/{addressId}/restore")
    public ResponseEntity<Void> restore(
            @PathVariable Long addressId
    ) {
        Long userId = currentUserService.getCurrentUserId();
        boolean ok = addressService.restore(userId, addressId);
        return ok ? ResponseEntity.noContent().build()
                : ResponseEntity.notFound().build();
    }

    @PatchMapping("/{addressId}/default")
    public ResponseEntity<Void> setDefault(
            @PathVariable Long addressId
    ) {
        Long userId = currentUserService.getCurrentUserId();
        boolean updated = addressService.setDefault(userId, addressId);
        return updated ? ResponseEntity.noContent().build()
                : ResponseEntity.notFound().build();
    }
    @GetMapping("/default")
    public ResponseEntity<AddressResponse> getDefault() {
        Long userId = currentUserService.getCurrentUserId();
        AddressResponse address = addressService.getDefaultAddress(userId);
        return ResponseEntity.ok(address);
    }
    @GetMapping
    public ResponseEntity<List<AddressResponse>> getAddresses() {
        Long userId = currentUserService.getCurrentUserId();
        List<AddressResponse> addresses = addressService.getAddresses(userId);
        return ResponseEntity.ok(addresses);
    }

}
