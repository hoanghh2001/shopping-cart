package hoang.shop.identity.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record AddressCreateRequest(
        Long userId,

        @NotBlank(message = "{address.fullName.notBlank}")
        @Size(max = 100, message = "{address.fullName.size}")
        String fullName,

        @NotBlank(message = "{address.phone.notBlank}")
        @Size(max = 16, message = "{address.phone.size}")
        @Pattern(regexp = "^\\+?[1-9]\\d{1,14}$",
                message = "{address.phone.size}")
        String phone,

        @NotBlank(message = "{address.postalCode.notBlank}")
        @Pattern(regexp = "^\\d{3}-?\\d{4}$",
                message = "{address.postalCode.pattern}")
        String postalCode,

        @NotBlank(message = "{address.addressLine1.pattern}")
        String addressLine1,


        String addressLine2,

        @NotBlank(message = "{address.city.notBlank}")
        String city,

        @NotBlank(message = "{address.state.notBlank}")
        String state,

        @NotBlank(message = "{address.state.country}")
        String country
) {
}
