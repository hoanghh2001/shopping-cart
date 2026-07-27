package hoang.shop.identity;

import hoang.shop.identity.security.UserPrincipal;
import hoang.shop.identity.service.CurrentUserServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
@ExtendWith(MockitoExtension.class)
public class CurrentUserServiceTest {
    @InjectMocks
    private  CurrentUserServiceImpl currentUserService;
    @Test
    void getCurrentUserId_ShouldReturnUserId() {
        // fake principal
        UserPrincipal principal = new UserPrincipal(1L, "testuser", "password", null, "Test User", null, null);

        UsernamePasswordAuthenticationToken auth =
                new UsernamePasswordAuthenticationToken(principal, null);

        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(auth);
        SecurityContextHolder.setContext(context);
        // Act
        Long userId = currentUserService.getCurrentUserId();

        // Assert

        assertNotNull(userId);
        assertTrue(userId > 0);
    }


}
