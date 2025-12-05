package hoang.shop.cart.controller;

import lombok.RequiredArgsConstructor;
import hoang.shop.cart.dto.request.CartItemCreateRequest;
import hoang.shop.cart.dto.request.CartItemUpdateRequest;
import hoang.shop.cart.dto.response.CartItemResponse;
import hoang.shop.cart.service.CartItemService;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users/{userId}")
@RequiredArgsConstructor
@Validated
public class CartItemController {
    private final CartItemService cartItemService;


    @PostMapping("/cart-items")
    public ResponseEntity<CartItemResponse> create(@PathVariable Long userId,
                                                   @RequestBody CartItemCreateRequest req) {
        return ResponseEntity.ok(cartItemService.create(userId, req));
    }

    @GetMapping("/carts/{cartId}/cart-items")
    public ResponseEntity<List<CartItemResponse>> findAllByCartId(@PathVariable Long userId, @PathVariable Long cartId) {
        return ResponseEntity.ok(cartItemService.findAllByCartId(cartId));
    }
    @GetMapping("/cart-items")
    public ResponseEntity<List<CartItemResponse>> findAllByUserId(@PathVariable Long userId) {
        return ResponseEntity.ok(cartItemService.findAllByUserId(userId));
    }

    @PatchMapping("/cart-items/{cartItemId}")
    public ResponseEntity<CartItemResponse> update(@PathVariable Long userId,
                                                   @PathVariable Long cardItemId,
                                                   @RequestBody CartItemUpdateRequest req) {
        return ResponseEntity.ok(cartItemService.update(userId, cardItemId, req));
    }

    @DeleteMapping("/cart-items/{cartItemId}")
    public ResponseEntity<Void> delete(@PathVariable Long userId,
                                       @PathVariable Long cartItemId
                                       ) {
        cartItemService.delete(userId, cartItemId);
        return ResponseEntity.noContent().build();
    }
}