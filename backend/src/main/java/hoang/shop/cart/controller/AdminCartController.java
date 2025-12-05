package hoang.shop.cart.controller;

import hoang.shop.cart.dto.response.ItemResponse;
import lombok.RequiredArgsConstructor;
import hoang.shop.cart.dto.request.CartItemUpdateRequest;
import hoang.shop.cart.service.CartItemService;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@Validated
public class AdminCartController {
    private final CartItemService cartItemService;

    @GetMapping("/carts/{cartId}/cart-items")
    public ResponseEntity<List<ItemResponse>> findAllByCartId(@PathVariable Long userId, @PathVariable Long cartId) {
        return ResponseEntity.ok(cartItemService.findAllByCartId(cartId));
    }
    @GetMapping("/cart-items")
    public ResponseEntity<List<ItemResponse>> findAllByUserId(@PathVariable Long userId) {
        return ResponseEntity.ok(cartItemService.findAllByUserId(userId));
    }

    @PatchMapping("/cart-items/{cartItemId}")
    public ResponseEntity<ItemResponse> update(@PathVariable Long userId,
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