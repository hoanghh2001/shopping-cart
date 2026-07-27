package hoang.shop.order.controller.user;

import hoang.shop.identity.service.CurrentUserService;
import hoang.shop.order.dto.request.OrderUpdateRequest;
import hoang.shop.order.dto.response.OrderResponse;
import hoang.shop.order.dto.response.OrderStatusHistoryResponse;
import hoang.shop.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/my-order")
@RequiredArgsConstructor
public class UserOrderController {
    private final OrderService orderService;
    private final CurrentUserService currentUserService;

    @PutMapping("/orders/{orderNumber}")
    public ResponseEntity<OrderResponse> updateOrderByUser(@PathVariable String orderNumber, @RequestBody OrderUpdateRequest request) {
        Long userId = currentUserService.getCurrentUserId();
        OrderResponse body = orderService.updateOrderByUser(userId, orderNumber, request);
        return ResponseEntity.ok(body);
    }

    @PatchMapping("/orders/{orderNumber}/cancel")
    public ResponseEntity<OrderResponse> cancelOrderByUser(@PathVariable String orderNumber, @RequestBody String reason) {
        Long userId = currentUserService.getCurrentUserId();
        OrderResponse body = orderService.cancelOrderByUser(userId, orderNumber, reason);
        return ResponseEntity.ok(body);
    }

    @GetMapping("/orders/{orderNumber}")
    public ResponseEntity<OrderResponse> getOrderDetailForUser(@PathVariable String orderNumber) {
        Long userId = currentUserService.getCurrentUserId();
        OrderResponse body = orderService.getOrderDetailForUser(userId, orderNumber);
        return ResponseEntity.ok(body);
    }

    @GetMapping("/orders")
    public ResponseEntity<Page<OrderResponse>> findOrdersOfUser(
            @PageableDefault(
                    size = 10,
                    sort = "placedAt",
                    direction = Sort.Direction.DESC) Pageable pageable) {
        Long userId = currentUserService.getCurrentUserId();
        Page<OrderResponse> body = orderService.findOrdersOfUser(userId, pageable);
        return ResponseEntity.ok(body);
    }

    @GetMapping("/orders/{orderId}/status-histories")
    public ResponseEntity<List<OrderStatusHistoryResponse>> findStatusHistoryByOrderIdForUser(
            @PathVariable Long orderId) {
        Long userId = currentUserService.getCurrentUserId();
        List<OrderStatusHistoryResponse> body = orderService.findStatusHistoryByOrderIdForUser(userId, orderId);
        return ResponseEntity.ok(body);
    }


}
