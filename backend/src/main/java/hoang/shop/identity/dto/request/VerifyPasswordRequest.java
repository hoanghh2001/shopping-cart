package hoang.shop.identity.dto.request;

import jakarta.validation.constraints.NotBlank;

public record VerifyPasswordRequest(
        @NotBlank
        String password
) {
}
