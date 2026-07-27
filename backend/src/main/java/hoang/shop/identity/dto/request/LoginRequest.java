package hoang.shop.identity.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
        @NotBlank(message = "{error.email.required}")
        @Email(message = "{error.email.invalid}")
        String email,
        @NotBlank(message = "{error.password.required}")
        String password
) {
}
