package hoang.shop.cart.service;

import hoang.shop.cart.dto.request.CartItemCreateRequest;
import hoang.shop.cart.dto.request.CartItemUpdateRequest;
import hoang.shop.cart.dto.response.ItemResponse;

import java.util.List;

public interface CartItemService {
    List<ItemResponse> findAllByCartId(Long cartId);

    ItemResponse create(Long userId, CartItemCreateRequest request);

    ItemResponse update(Long userId, Long itemId, CartItemUpdateRequest request);

    boolean delete(Long userId, Long itemId);

    List<ItemResponse> findAllByUserId(Long userId);
}
