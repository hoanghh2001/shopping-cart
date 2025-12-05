package hoang.shop.identity.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.Instant;

public record AddressUpdateRequest(
        @NotBlank(message = "error.address.name.not-blank")
        String name,
        @NotBlank(message = "error.address.phone.not-blank")
        String phone,

        @NotBlank(message = "error.address.postal-code.not-blank")
        @Pattern(
                regexp = "^\\d{3}-?\\d{4}$",
                message = "error.address.postal-code.format")
        String postalCode,
        @NotBlank(message = "error.address.prefecture.not-blank")
        @Pattern(
                regexp = "^.+[都道府県]$",
                message = "{error.address.prefecture.invalid}")
        String prefecture,
        @NotBlank(message = "error.address.municipality.not-blank")
        @Pattern(
                regexp = "^.+[市区町村]$",
                message = "{error.address.municipality.invalid}")
        String municipality,
        @NotBlank(message = "error.address.streetNumber.not-blank")
        @Pattern(
                regexp = "^([0-9]+(-[0-9]+){1,2}|[0-9]+丁目[0-9]+番([0-9]+号)?)$",
                message = "{error.address.street-number.invalid}")
        String streetNumber,
        String building,
        String roomNumber
){
}
