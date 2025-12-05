package hoang.shop.cart.controller;

import hoang.shop.cart.dto.request.CheckoutRequest;
import hoang.shop.identity.service.CurrentUserService;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import hoang.shop.cart.dto.request.CartItemCreateRequest;
import hoang.shop.cart.dto.request.CartItemUpdateRequest;
import hoang.shop.cart.dto.response.CartResponse;
import hoang.shop.order.dto.response.OrderResponse;
import hoang.shop.cart.service.CartService;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/my-cart")
@RequiredArgsConstructor
@Validated
public class CartController {

    private final CartService cartService;
    private final CurrentUserService currentUserService;

    @GetMapping
    public ResponseEntity<CartResponse> getMyCart() {
        Long userId = currentUserService.getCurrentUserId();
        return ResponseEntity.ok(cartService.getMyCart(userId));
    }

    @PostMapping("/items")
    public ResponseEntity<CartResponse> addItem(@RequestBody CartItemCreateRequest request) {
        Long userId = currentUserService.getCurrentUserId();
        return ResponseEntity.ok(cartService.addItem(userId, request));
    }

    @PutMapping("/items/{itemId}")
    public ResponseEntity<CartResponse> updateItem(@PathVariable Long itemId,
                                                   @RequestBody CartItemUpdateRequest request) {
        Long userId = currentUserService.getCurrentUserId();
        return ResponseEntity.ok(cartService.updateItem(userId, itemId, request));
    }

    @DeleteMapping("/items/{itemId}")
    public ResponseEntity<Void> removeItem(@PathVariable Long itemId) {
        Long userId = currentUserService.getCurrentUserId();
        cartService.removeItem(userId, itemId);
        return ResponseEntity.noContent().build();
    }
//
//    @DeleteMapping("/items")
//    public ResponseEntity<Void> clearItems() {
//        Long userId = currentUserService.getCurrentUserId();
//        cartService.clearItems(userId);
//        return ResponseEntity.noContent().build();
//    }

    @PostMapping("/checkout")
    public ResponseEntity<OrderResponse> checkout(@RequestBody CheckoutRequest request) {
        Long userId = currentUserService.getCurrentUserId();
        return ResponseEntity.ok(cartService.checkout(userId,request));
    }
}
