package hoang.shop.identity.dto.request;

public record ChangePasswordRequest(
        String oldPassword,
        String newPassword
) {
}
