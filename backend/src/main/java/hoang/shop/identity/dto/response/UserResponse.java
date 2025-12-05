package hoang.shop.identity.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.Instant;
import java.time.LocalDate;
@JsonInclude(JsonInclude.Include.NON_NULL)
public record UserResponse(
        Long id,
        String firstName,
        String lastName,
        String fullName,
        String gender,
        @JsonFormat(pattern = "yyyy-MM-dd")
        LocalDate birthday,
        String email,
        String phone,
        String avatarUrl,
        Instant createdAt,
        Long createdBy,
        Instant updatedAt,
        Long updatedBy,
        Instant deletedAt,
        Long deletedBy,
        boolean deleted,
        boolean emailVerified
) {

}
