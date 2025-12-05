package hoang.shop.identity.dto.request;

import jakarta.validation.constraints.*;
import hoang.shop.common.enums.Gender;

import java.time.LocalDate;

public record UserUpdateRequest(
        @Size(min = 1, max = 100, message = "{user.firstName.size}")
        @Pattern(regexp = "^(?:[\\p{L}\\p{M}]|[\\p{L}\\p{M}][\\p{L}\\p{M}\\s'’.\\-・ー]{0,98}[\\p{L}\\p{M}])$",
                message = "{user.firstName.notBlank}")
        String firstName,

        @Size(min = 1, max = 100, message = "{user.lastName.size}")
        @Pattern(regexp = "^(?:[\\p{L}\\p{M}]|[\\p{L}\\p{M}][\\p{L}\\p{M}\\s'’.\\-・ー]{0,98}[\\p{L}\\p{M}])$",
                message = "{user.lastName.notBlank}")
        String lastName,

        Gender gender,

        @Past(message = "{user.birthday.past}")
        LocalDate birthday,

        @Email(message = "{user.email.invalid")
        @Size(max = 255,message = "{user.email.size}")
        String email,

        @Pattern(regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\\\d).{8,}$",message = "user.password.weak")
        @Size(min = 8,max = 72 ,message = "{user.password.size}")
        String password,

        @Pattern(regexp="^\\\\+[1-9]\\\\d{1,14}$",message = "{user.phone.e164}")
        String phone,

        @Size(max = 255,message = "{user.avatarUrl.size}")
        String avatarUrl,

        Boolean emailVerified
) {
}
