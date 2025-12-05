package hoang.shop.identity.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.Instant;

public record AddressUpdateRequest(
        @Size(max = 100, message = "{address.fullName.size}")
        @Pattern(regexp = "^[\\p{L}\\p{M}\\s'.-]+$", message = "{address.fullName.pattern}")
        String fullName,

        @Size(max = 16, message = "{address.phone.size}")
        @Pattern(regexp = "^(\\+\\d{1,3}[- ]?)?\\d{10,15}$",
                message = "{address.phone.size}")
        String phone,

        @Pattern(regexp = "^\\\\d{3}-?\\\\d{4}$",
                message = "{address.postalCode.pattern}")
        String postalCode,

        String addressLine1,
        String addressLine2,
        String city,
        String state,
        String country,
        boolean isDefault
){
}
