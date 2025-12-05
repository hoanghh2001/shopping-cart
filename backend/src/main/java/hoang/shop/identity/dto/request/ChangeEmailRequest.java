package hoang.shop.identity.dto.request;

public record ChangeEmailRequest (
        String newEmail,
        String password
){ }
