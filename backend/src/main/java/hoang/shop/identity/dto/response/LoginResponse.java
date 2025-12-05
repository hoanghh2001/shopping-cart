package hoang.shop.identity.dto.response;

public record LoginResponse(
        UserResponse user,
        String accessToken,
        String refreshToken
) {
}
