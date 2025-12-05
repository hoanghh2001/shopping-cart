package hoang.shop.identity.controller.user;

import hoang.shop.identity.dto.request.ChangeEmailRequest;
import hoang.shop.identity.dto.request.ChangePasswordRequest;
import hoang.shop.identity.dto.request.DeleteAccountRequest;
import hoang.shop.identity.dto.response.MyAccountResponse;
import hoang.shop.identity.dto.response.SessionInfoResponse;
import hoang.shop.identity.service.MyAccountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/my-account")
@RequiredArgsConstructor
public class MyAccountController {

    private final MyAccountService myAccountService;

    @GetMapping
    public ResponseEntity<MyAccountResponse> getMyAccount() {
        MyAccountResponse account = myAccountService.getMyAccount();
        return ResponseEntity.ok(account);
    }

    @PutMapping("/change-password")
    public ResponseEntity<Void> changePassword(
            @RequestBody @Valid ChangePasswordRequest request
    ) {
        myAccountService.changePassword(request);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/change-email")
    public ResponseEntity<Void> changeEmail(
            @RequestBody @Valid ChangeEmailRequest request
    ) {
        myAccountService.changeEmail(request);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteAccount(
            @RequestBody @Valid DeleteAccountRequest request
    ) {
        myAccountService.deleteAccount(request);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/sessions")
    public ResponseEntity<List<SessionInfoResponse>> getSessions() {
        return ResponseEntity.ok(myAccountService.getSessions());
    }

    @DeleteMapping("/sessions/{sessionId}")
    public ResponseEntity<Void> revokeSession(
            @PathVariable Long sessionId
    ) {
        myAccountService.revokeSession(sessionId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/sessions")
    public ResponseEntity<Void> revokeAllSessions() {
        myAccountService.revokeAllSessions();
        return ResponseEntity.noContent().build();
    }
}
