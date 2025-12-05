package hoang.shop.identity.controller.user;

import hoang.shop.identity.dto.request.ForgotPasswordRequest;
import hoang.shop.identity.dto.request.LoginRequest;
import hoang.shop.identity.dto.request.RegisterRequest;
import hoang.shop.identity.dto.request.ResetPasswordRequest;
import hoang.shop.identity.dto.response.AuthResponse;
import hoang.shop.identity.dto.response.LoginResponse;
import hoang.shop.identity.dto.response.UserResponse;
import hoang.shop.identity.service.AuthService;
import hoang.shop.identity.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(@RequestBody @Valid RegisterRequest request) {
        var saved = authService.register(request);
        return ResponseEntity.ok(saved);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody @Valid LoginRequest request) {
        var user = authService.login(request);
        return ResponseEntity.ok(user);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout() {
        authService.logout(null);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<Void> forgotPassword(@RequestBody @Valid ForgotPasswordRequest request) {
        authService.forgotPassword(request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/reset-password")
    public ResponseEntity<Void> resetPassword(@RequestBody @Valid ResetPasswordRequest request) {
        authService.resetPassword(request);
        return ResponseEntity.ok().build();
    }



}
