package hoang.shop.identity.dto.request;

public record AuthResponse (
    String accessToken,
    String refreshToken
){}
