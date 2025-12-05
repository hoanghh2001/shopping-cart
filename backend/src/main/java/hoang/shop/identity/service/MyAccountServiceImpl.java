package hoang.shop.identity.service;

import hoang.shop.common.exception.NotFoundException;
import hoang.shop.identity.dto.request.ChangeEmailRequest;
import hoang.shop.identity.dto.request.ChangePasswordRequest;
import hoang.shop.identity.dto.request.DeleteAccountRequest;
import hoang.shop.identity.dto.request.VerifyPasswordRequest;
import hoang.shop.identity.dto.response.MyAccountResponse;
import hoang.shop.identity.dto.response.SessionInfoResponse;
import hoang.shop.identity.mapper.MyAccountMapper;
import hoang.shop.identity.mapper.UserMapper;
import hoang.shop.identity.model.User;
import hoang.shop.identity.model.UserSession;
import hoang.shop.identity.repository.UserRepository;
import hoang.shop.identity.repository.UserSessionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MyAccountServiceImpl implements MyAccountService {

    private final CurrentUserService currentUserService;
    private final UserRepository userRepository;
    private final UserSessionRepository userSessionRepository;
    private final MyAccountMapper myAccountMapper;


    @Override
    public MyAccountResponse getMyAccount() {
        Long userId = currentUserService.getCurrentUserId();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("{error.user.id.not-found}"));
        return myAccountMapper.fromUser(user);
    }

    @Override
    public void changePassword(ChangePasswordRequest request) {
        Long userId = currentUserService.getCurrentUserId();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("{error.user.id.not-found}"));
    }

    @Override
    public void changeEmail(ChangeEmailRequest request) {
        Long userId = currentUserService.getCurrentUserId();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("{error.user.id.not-found}"));
        user.setEmail(request.newEmail());
    }

    @Override
    public void deleteAccount(DeleteAccountRequest request) {
        Long userId = currentUserService.getCurrentUserId();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("{error.user.id.not-found}"));
    }


    @Override
    public List<SessionInfoResponse> getSessions() {
        Long userId = currentUserService.getCurrentUserId();
        Instant now = Instant.now();

        List<UserSession> sessions = userSessionRepository
                .findByUser_IdAndRevokedAtIsNullAndExpiresAtAfter(userId, now);

        return sessions.stream()
                .map(s -> new SessionInfoResponse(
                        s.getId(),
                        s.getDevice(),
                        s.getIpAddress(),
                        s.getCreatedAt(),
                        s.getExpiresAt(),
                        false
                ))
                .toList();
    }

    @Override
    public void revokeSession(Long sessionId) {
        Long userId = currentUserService.getCurrentUserId();
        int updated = userSessionRepository
                .revokeByIdAndUserId(sessionId, userId, Instant.now());
        if (updated == 0) {
            throw new NotFoundException("{error.user-session.id.not-found}");
        }
    }

    @Override
    public void revokeAllSessions() {
        Long userId = currentUserService.getCurrentUserId();
        userSessionRepository.revokeAllByUserId(userId, Instant.now());
    }
}
