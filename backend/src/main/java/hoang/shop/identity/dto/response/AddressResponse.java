package hoang.shop.identity.dto.response;

public record AddressResponse(
        Long id,
        String name,
        String phone,
        String postalCode,
        String fullAddress,
        String prefecture,
        String municipality,
        Integer chome,
        Integer ban,
        Integer go,
        String streetNumber,
        String building,
        String roomNumber,
        Boolean isDefault

) {
}
