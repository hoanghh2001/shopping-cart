package hoang.shop.identity.dto.request;

public record ResetPasswordRequest (
        String token,
        String newPassword
){
}
