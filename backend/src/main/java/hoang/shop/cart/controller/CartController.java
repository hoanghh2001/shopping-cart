package hoang.shop.cart.controller;

import hoang.shop.cart.dto.request.CheckoutRequest;
import hoang.shop.cart.dto.request.ShippingEstimateRequest;
import hoang.shop.cart.dto.response.CartSummary;
import hoang.shop.cart.dto.response.ShippingEstimateResponse;
import hoang.shop.common.enums.ShippingRegion;
import hoang.shop.identity.service.CurrentUserService;
import jakarta.validation.Valid;
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

import java.util.Map;

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


    @GetMapping("/summary")
    public ResponseEntity<CartSummary> getQuantity() {
        Long userId = currentUserService.getCurrentUserId();
        return ResponseEntity.ok(cartService.getQuantity(userId));
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
    public ResponseEntity<OrderResponse> checkout(@RequestBody @Valid CheckoutRequest request) {
        Long userId = currentUserService.getCurrentUserId();
        return ResponseEntity.ok(cartService.checkout(userId, request));
    }

    @PostMapping("/shipping-estimate")
    public ShippingEstimateResponse estimate(@RequestBody ShippingEstimateRequest req) {
        Long userId = currentUserService.getCurrentUserId();
        return cartService.estimate(userId, req.addressId());
    }
}
