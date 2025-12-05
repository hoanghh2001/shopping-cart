package hoang.shop.identity.dto.request;

public record LoginRequest(
        String email,
        String password
) {
}
