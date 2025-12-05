package hoang.shop.identity.service;

import hoang.shop.common.exception.BadRequestException;
import hoang.shop.identity.dto.request.ForgotPasswordRequest;
import hoang.shop.identity.dto.request.LoginRequest;
import hoang.shop.identity.dto.request.RegisterRequest;
import hoang.shop.identity.dto.request.ResetPasswordRequest;
import hoang.shop.identity.dto.response.AuthResponse;
import hoang.shop.identity.dto.response.UserResponse;
import hoang.shop.identity.mapper.UserMapper;
import hoang.shop.identity.model.User;
import hoang.shop.identity.model.UserSession;
import hoang.shop.identity.repository.UserRepository;
import hoang.shop.identity.repository.UserSessionRepository;
import hoang.shop.identity.security.JwtTokenProvider;
import hoang.shop.identity.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@Transactional
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final UserSessionRepository userSessionRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public UserResponse register(RegisterRequest request) {
        // ví dụ check email trùng – tuỳ bạn có muốn không
        if (userRepository.existsByEmail(request.email())) {
            throw new BadRequestException("{error.auth.email.duplicated}");
        }

        User user = new User();
        user.setEmail(request.email());
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setCreatedAt(Instant.now());
        User saved = userRepository.save(user);
        return userMapper.toResponse(saved);
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        // 1) Xác thực bằng Spring Security
        Authentication authToken = new UsernamePasswordAuthenticationToken(
                request.email(),          // principal (email)
                request.password()        // credentials (mật khẩu)
        );

        Authentication authentication = authenticationManager.authenticate(authToken);
        UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();

        // 2) Lấy User từ DB
        User user = userRepository.findById(principal.getId())
                .orElseThrow(() -> new IllegalStateException("User not found after authentication"));

        user.setLastLogin(Instant.now());

        // 3) Tạo JWT access token
        String accessToken = jwtTokenProvider.generateToken(principal);
        // Nếu bạn có hàm riêng:
        // String accessToken = jwtTokenProvider.generateAccessToken(principal);

        // 4) (Optional) Tạo refresh token + session nếu bạn muốn dùng
        // Nếu JwtTokenProvider của bạn có generateRefreshToken thì bật đoạn này lên
        /*
        String refreshToken = jwtTokenProvider.generateRefreshToken(principal.getId());
        Instant now = Instant.now();
        Instant expiresAt = now.plusMillis(jwtTokenProvider.getRefreshTokenTtlMs());

        UserSession session = UserSession.builder()
                .user(user)
                .refreshToken(refreshToken)
                .device(null)      // sau này lấy từ HttpServletRequest (User-Agent)
                .ipAddress(null)   // sau này lấy từ HttpServletRequest.getRemoteAddr()
                .createdAt(now)
                .expiresAt(expiresAt)
                .build();

        userSessionRepository.save(session);
        */

        userRepository.save(user);

        // 5) Map sang UserResponse
        UserResponse userResponse = userMapper.toResponse(user);

        // 6) Trả về AuthResponse (hiện tại bạn đang dùng 2 tham số: token + user)
        return new AuthResponse(accessToken, userResponse);
        // Nếu sau này AuthResponse có thêm refreshToken thì đổi thành:
        // return new AuthResponse(accessToken, refreshToken, userResponse);
    }

    @Override
    public void logout(String refreshToken) {
        if (refreshToken == null || refreshToken.isBlank()) {
            return;
        }
        userSessionRepository.findByRefreshTokenAndRevokedAtIsNullAndExpiresAtAfter(
                        refreshToken, Instant.now())
                        .ifPresent(UserSession::revoke);
    }

    @Override
    public void forgotPassword(ForgotPasswordRequest request) {
        // TODO: generate token reset, lưu DB + gửi mail
    }

    @Override
    public void resetPassword(ResetPasswordRequest request) {
        // TODO: verify token reset, set mật khẩu mới
    }
}
