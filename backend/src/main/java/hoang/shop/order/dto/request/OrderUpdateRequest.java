package hoang.shop.order.dto.request;

public record OrderUpdateRequest(
        Long newAddressId,
        String note
) {
}
