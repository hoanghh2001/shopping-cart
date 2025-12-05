package hoang.shop.identity.service;

import hoang.shop.identity.model.Role;
import hoang.shop.identity.model.User;
import hoang.shop.identity.model.UserRole;
import hoang.shop.identity.repository.UserRepository;
import hoang.shop.identity.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {
    private final UserRepository userRepository;
    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + email));
        Collection<? extends GrantedAuthority> authorities = user.getUserRoles().stream()
                .map(UserRole::getRole)
                .map(Role::getName)
                .map(name -> "ROLE_" + name)
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toSet());

        return UserPrincipal.from(user, authorities);
    }
}
