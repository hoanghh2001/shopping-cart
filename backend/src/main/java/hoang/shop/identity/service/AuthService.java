package hoang.shop.identity.service;

import hoang.shop.identity.dto.request.ForgotPasswordRequest;
import hoang.shop.identity.dto.request.LoginRequest;
import hoang.shop.identity.dto.request.RegisterRequest;
import hoang.shop.identity.dto.request.ResetPasswordRequest;
import hoang.shop.identity.dto.response.AuthResponse;
import hoang.shop.identity.dto.response.LoginResponse;
import hoang.shop.identity.dto.response.UserResponse;

public interface AuthService {

    UserResponse register(RegisterRequest req);

    AuthResponse login(LoginRequest request);
    void logout(String refreshToken);

    void forgotPassword(ForgotPasswordRequest request);

    void resetPassword(ResetPasswordRequest request);
}
