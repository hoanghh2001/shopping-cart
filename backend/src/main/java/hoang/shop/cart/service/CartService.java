package hoang.shop.cart.service;

import hoang.shop.cart.dto.request.CartItemCreateRequest;
import hoang.shop.cart.dto.request.CartItemUpdateRequest;
import hoang.shop.cart.dto.response.CartResponse;
import hoang.shop.order.dto.response.OrderResponse;

public interface CartService {
    CartResponse findActiveCart(Long userId);
    CartResponse addItem(Long userId, CartItemCreateRequest request);
    CartResponse updateItem(Long userId, Long itemId, CartItemUpdateRequest request);
    boolean removeItem(Long userId, Long itemId);
    void clearItems(Long userId);
    OrderResponse checkout(Long userId);


}
