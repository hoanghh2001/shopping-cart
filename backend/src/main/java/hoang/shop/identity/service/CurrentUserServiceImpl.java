package hoang.shop.identity.service;

import hoang.shop.common.exception.NotFoundException;
import hoang.shop.identity.model.User;
import hoang.shop.identity.repository.UserRepository;
import hoang.shop.identity.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CurrentUserServiceImpl implements CurrentUserService {

    private final UserRepository userRepository;

    @Override
    public Long getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof UserPrincipal principal)) {
            return null;
        }
        return principal.getId();
    }

    @Override
    public User getCurrentUser() {
        Long id = getCurrentUserId();
        if (id == null) {
            throw new NotFoundException("{error.user.current.not-found}");
        }
        return userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("{error.user.id.not-found}"));
    }
}
