package hoang.shop.order.controller.user;

import hoang.shop.common.enums.OrderStatus;
import hoang.shop.identity.service.CurrentUserService;
import hoang.shop.order.dto.request.OrderUpdateRequest;
import hoang.shop.order.dto.response.OrderItemResponse;
import hoang.shop.order.dto.response.OrderResponse;
import hoang.shop.order.dto.response.OrderStatusHistoryResponse;
import hoang.shop.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/my-order")
@RequiredArgsConstructor
public class UserOrderController {
    private final OrderService orderService;
    private final CurrentUserService currentUserService;
    @PutMapping("/orders/{orderId}")
    public ResponseEntity<OrderResponse> updateOrderByUser(@PathVariable Long orderId,@RequestBody OrderUpdateRequest request) {
        Long userId = currentUserService.getCurrentUserId();
        OrderResponse body =  orderService.updateOrderByUser(userId, orderId, request);
        return ResponseEntity.ok(body);
    }
    @PatchMapping("/orders/{orderId}/cancel")
    public ResponseEntity<OrderResponse> cancelOrderByUser(@PathVariable Long orderId,@RequestBody String reason) {
        Long userId = currentUserService.getCurrentUserId();
        OrderResponse body =  orderService.cancelOrderByUser(userId, orderId, reason);
        return ResponseEntity.ok(body);
    }
    @GetMapping("/orders/{orderId}")
    public ResponseEntity<OrderResponse> getOrderDetailForUser(@PathVariable Long orderId) {
        Long userId = currentUserService.getCurrentUserId();
        OrderResponse body =  orderService.getOrderDetailForUser(userId, orderId);
        return ResponseEntity.ok(body);
    }
    @GetMapping("/orders")
    public ResponseEntity<List<OrderResponse>>findOrdersOfUser() {
        Long userId = currentUserService.getCurrentUserId();
        List<OrderResponse> body =  orderService.findOrdersOfUser(userId);
        return ResponseEntity.ok(body);
    }
//    @GetMapping("/orders/status")
//    public ResponseEntity<List<OrderResponse>>findOrdersOfUserByStatus(@RequestParam OrderStatus status) {
//        Long userId = currentUserService.getCurrentUserId();
//        List<OrderResponse> body = orderService.findOrdersOfUserByStatus(userId, status);
//        return ResponseEntity.ok(body);
//    }
//    @GetMapping("/order-items/{orderItemId}")
//    public ResponseEntity<OrderItemResponse> getOrderItemForUser(
//            @PathVariable Long orderItemId) {
//        Long userId = currentUserService.getCurrentUserId();
//        OrderItemResponse body =  orderService.getOrderItemForUser(userId, orderItemId);
//        return ResponseEntity.ok(body);
//    }
//    @GetMapping("/orders/{orderId}/items")
//    public ResponseEntity<Slice<OrderItemResponse>> findItemsOfOrderForUser(
//            @PathVariable Long orderId,
//            Pageable pageable) {
//        Long userId = currentUserService.getCurrentUserId();
//        Slice<OrderItemResponse> body = orderService.findItemsOfOrderForUser(userId, orderId,pageable);
//        return ResponseEntity.ok(body);
//    }
    @GetMapping("/orders/{orderId}/status-histories")
    public ResponseEntity<List<OrderStatusHistoryResponse>> findStatusHistoryByOrderIdForUser(
            @PathVariable Long orderId) {
        Long userId = currentUserService.getCurrentUserId();
        List<OrderStatusHistoryResponse> body = orderService.findStatusHistoryByOrderIdForUser(userId, orderId);
        return ResponseEntity.ok(body);
    }




}
