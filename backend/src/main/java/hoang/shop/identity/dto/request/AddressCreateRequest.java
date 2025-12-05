package hoang.shop.identity.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;

public record AddressCreateRequest(
        String name,

        String phone,

        @Pattern(
                regexp = "^\\d{3}-?\\d{4}$",
                message = "error.address.postal-code.format")
        String postalCode,

        @Pattern(
                regexp = "^.+[都道府県]$",
                message = "{error.address.prefecture.invalid}")
        String prefecture,

        @Pattern(
                regexp = "^.+[市区町村]$",
                message = "{error.address.municipality.invalid}")
        String municipality,

        @Pattern(
                regexp = "^([0-9]+(-[0-9]+){1," +
                        "2}|[0-9]+丁目[0-9]+番([0-9]+号)?)$",
                message = "{error.address.street-number.invalid}")
        String streetNumber,

        String building,

        String roomNumber
) {
}
