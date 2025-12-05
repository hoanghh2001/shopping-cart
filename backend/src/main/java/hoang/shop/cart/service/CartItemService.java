package hoang.shop.cart.service;

import hoang.shop.cart.dto.request.CartItemCreateRequest;
import hoang.shop.cart.dto.request.CartItemUpdateRequest;
import hoang.shop.cart.dto.response.CartItemResponse;

import java.util.List;

public interface CartItemService {
    List<CartItemResponse> findAllByCartId(Long cartId);

    CartItemResponse create(Long userId, CartItemCreateRequest request);

    CartItemResponse update(Long userId, Long itemId, CartItemUpdateRequest request);

    boolean delete(Long userId, Long itemId);

}
