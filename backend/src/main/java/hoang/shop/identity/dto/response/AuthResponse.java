package hoang.shop.identity.dto.response;

public record AuthResponse(
        String accessToken,
        String refreshToken,
        UserResponse user
) {
}
