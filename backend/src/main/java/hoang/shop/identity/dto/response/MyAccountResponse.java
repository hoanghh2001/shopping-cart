package hoang.shop.identity.dto.response;

import java.util.List;

public record MyAccountResponse(
        Long id,
        String email,
        String fullName,
        List<String> roles,
        String avatarUrl
) {
}
