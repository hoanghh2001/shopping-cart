package hoang.shop.identity.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.Instant;
@JsonInclude(JsonInclude.Include.NON_NULL)
public record AddressResponse(
        Long id,
        Long userId,
        String fullName,
        String postalCode,
        String addressLine1,
        String addressLine2,
        String city,
        String state,
        String country,
        boolean isDefault,
        Instant createdAt,
        Long createdBy,
        Instant updatedAt,
        Long updatedBy,
        Instant deletedAt,
        Long deletedBy,
        boolean deleted


) {
}
