package hoang.shop.identity.service;

import hoang.shop.common.enums.status.UserStatus;
import hoang.shop.common.exception.*;
import hoang.shop.identity.dto.request.ForgotPasswordRequest;
import hoang.shop.identity.dto.request.LoginRequest;
import hoang.shop.identity.dto.request.RegisterRequest;
import hoang.shop.identity.dto.request.ResetPasswordRequest;
import hoang.shop.identity.dto.response.AuthResponse;
import hoang.shop.identity.dto.response.UserResponse;
import hoang.shop.identity.mapper.UserMapper;
import hoang.shop.identity.model.*;
import hoang.shop.identity.repository.RoleRepository;
import hoang.shop.identity.repository.UserRepository;
import hoang.shop.identity.repository.UserSessionRepository;
import hoang.shop.identity.repository.PasswordResetTokenRepository;
import hoang.shop.identity.security.JwtTokenProvider;
import hoang.shop.identity.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final RoleRepository roleRepository;
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final UserSessionRepository userSessionRepository;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public UserResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new DuplicateResourceException("{error.auth.email.duplicated}");
        }
        Role role = roleRepository.findByName("USER")
                .orElseThrow(() -> new NotFoundException("{error.role.name.not-found}"));
        Instant now = Instant.now();
        User user = new User();
        user.setEmail(request.email());
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setCreatedAt(now);
        user.setStatus(UserStatus.ACTIVE);
        Set<UserRole> userRoles = new HashSet<>();
        UserRole userRole = UserRole.builder()
                .user(user)
                .role(role)
                .build();
        userRoles.add(userRole);
        user.setUserRoles(userRoles);
        User saved = userRepository.save(user);
        return userMapper.toResponse(saved);
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        try {
            Authentication authToken = new UsernamePasswordAuthenticationToken(
                    request.email(),
                    request.password()
            );
            Authentication authentication = authenticationManager.authenticate(authToken);
            UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();
            User user = userRepository.findById(principal.getId())
                    .orElseThrow(() -> new IllegalStateException("User not found after authentication"));
            Instant now = Instant.now();
            user.setLastLogin(now);
            String accessToken = jwtTokenProvider.generateAccessToken(principal);
            String refreshToken = jwtTokenProvider.generateRefreshToken(principal.getId());
            Instant refreshExpiresAt = now.plusMillis(jwtTokenProvider.getRefreshTokenTtlMs());
            UserSession session = UserSession.builder()
                    .user(user)
                    .refreshToken(refreshToken)
                    .device(null)
                    .ipAddress(null)
                    .createdAt(now)
                    .expiresAt(refreshExpiresAt)
                    .build();

            userSessionRepository.save(session);
            userRepository.save(user);
            UserResponse userResponse = userMapper.toResponse(user);
            return new AuthResponse(accessToken, refreshToken, userResponse);
        } catch (BadCredentialsException | UsernameNotFoundException e) {
            throw new InvalidCredentialsException();
        }
    }

    @Override
    public void logout(String refreshToken) {
        if (refreshToken == null || refreshToken.isBlank()) {
            throw new NotFoundException("{error.refresh-token.not.found}");
        }
        userSessionRepository
                .findByRefreshTokenAndRevokedAtIsNullAndExpiresAtAfter(refreshToken, Instant.now())
                .ifPresent(UserSession::revoke);
    }

    @Override
    public void forgotPassword(ForgotPasswordRequest request) {
        User user = userRepository.findByEmail(request.email())
                .orElse(null);
        if (user == null) {
            return;
        }
        Instant now = Instant.now();
        passwordResetTokenRepository.invalidateAllForUser(user.getId(), now);
        String token = UUID.randomUUID().toString();
        Instant expiresAt = now.plusMillis(jwtTokenProvider.getPasswordResetTtlMs());
        PasswordResetToken resetToken = PasswordResetToken.builder()
                .user(user)
                .token(token)
                .createdAt(now)
                .expiresAt(expiresAt)
                .build();
        passwordResetTokenRepository.save(resetToken);
    }

    @Override
    public AuthResponse refresh(String refreshToken) {
        if (!jwtTokenProvider.validateRefreshToken(refreshToken)) {
            throw new UnauthorizedException("Invalid refresh token");
        }

        Long userId = jwtTokenProvider.getUserIdFromToken(refreshToken);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UnauthorizedException("User not found"));
        Collection<? extends GrantedAuthority> authorities = user.getUserRoles().stream()
                .map(UserRole::getRole)
                .map(Role::getName)
                .map(name -> "ROLE_" + name)
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toSet());
        UserPrincipal principal = UserPrincipal.from(user, authorities);
        Instant now = Instant.now();
        String newAccess = jwtTokenProvider.generateAccessToken(principal);
        String newRefresh = jwtTokenProvider.generateRefreshToken(user.getId());
        Instant refreshExpiresAt = now.plusMillis(jwtTokenProvider.getRefreshTokenTtlMs());
        UserSession session = UserSession.builder()
                .user(user)
                .refreshToken(refreshToken)
                .device(null)
                .ipAddress(null)
                .createdAt(now)
                .expiresAt(refreshExpiresAt)
                .build();

        userSessionRepository.save(session);
        userRepository.save(user);
        return new AuthResponse(newAccess, newRefresh, userMapper.toResponse(user));
    }

    @Override
    public void resetPassword(ResetPasswordRequest request) {
        PasswordResetToken resetToken = passwordResetTokenRepository
                .findByTokenAndUsedAtIsNull(request.token())
                .orElseThrow(() -> new BadRequestException("{error.password-reset.token.invalid}"));

        Instant now = Instant.now();
        if (resetToken.getExpiresAt().isBefore(now)) {
            throw new BadRequestException("{error.password-reset.token.expired}");
        }
        User user = resetToken.getUser();
        user.setPassword(passwordEncoder.encode(request.newPassword()));
        userRepository.save(user);
        resetToken.markUsed(now);
        passwordResetTokenRepository.save(resetToken);
        userSessionRepository.revokeAllActiveSessionsByUserId(user.getId(), now);
    }


}
