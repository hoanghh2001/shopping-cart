package hoang.shop.order.controller.admin;

import hoang.shop.identity.service.CurrentUserService;
import hoang.shop.order.dto.request.OrderSearchCondition;
import hoang.shop.order.dto.response.OrderItemResponse;
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
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminOrderController {
    private final OrderService orderService;
    private final CurrentUserService currentUserService;


    @GetMapping("/orders")
    public ResponseEntity<Page<OrderResponse>> findOrders(
            @ModelAttribute  OrderSearchCondition condition,
            @PageableDefault(direction = Sort.Direction.DESC,sort = "createdAt") Pageable pageable) {
        Page<OrderResponse> body = orderService.findOrders(condition, pageable);
        return ResponseEntity.ok(body);
    }
    @GetMapping("/orders/{orderId}")
    public ResponseEntity<OrderResponse> getOrder(@PathVariable Long orderId) {
        OrderResponse body = orderService.getOrder(orderId);
        return ResponseEntity.ok(body);
    }
    @PatchMapping("/orders/{orderId}/confirm")
    public ResponseEntity<OrderResponse> confirmOrder(@PathVariable Long orderId){
        Long adminId = currentUserService.getCurrentUserId();
        OrderResponse body = orderService.confirmOrder(orderId, adminId);
        return ResponseEntity.ok(body);
    }
    @PatchMapping("/orders/{orderId}/packing")
    public ResponseEntity<OrderResponse> markAsPacking(@PathVariable Long orderId) {
        Long adminId = currentUserService.getCurrentUserId();
        OrderResponse body = orderService.markAsPacking(orderId, adminId);
        return ResponseEntity.ok(body);
    }
    @PatchMapping("/orders/{orderId}/shipping")
    public ResponseEntity<OrderResponse> markAsShipping(@PathVariable Long orderId) {
        Long adminId = currentUserService.getCurrentUserId();
        OrderResponse body = orderService.markAsShipping(orderId, adminId);
        return ResponseEntity.ok(body);
    }
    @PatchMapping("/orders/{orderId}/delivered")
    public ResponseEntity<OrderResponse> markAsDelivered(@PathVariable Long orderId) {
        Long adminId = currentUserService.getCurrentUserId();
        OrderResponse body = orderService.markAsDelivered(orderId, adminId);
        return ResponseEntity.ok(body);
    }
    @PatchMapping("/orders/{orderId}/cancel")
    public ResponseEntity<OrderResponse> cancelOrderByAdmin(@PathVariable Long orderId,@RequestBody String reason) {
        Long adminId = currentUserService.getCurrentUserId();
        OrderResponse body = orderService.cancelOrderByAdmin(orderId, adminId, reason);
        return ResponseEntity.ok(body);
    }
    @GetMapping("/orders/{orderId}/order-status-histories")
    public ResponseEntity<List<OrderStatusHistoryResponse>> findStatusHistoryByOrderId(Long orderId) {
        List<OrderStatusHistoryResponse> body = orderService.findStatusHistoryByOrderId(orderId);
        return ResponseEntity.ok(body);
    }
    @GetMapping("/orders/{orderId}/order-items")
    public ResponseEntity<List<OrderItemResponse>> findItemsByOrderId(Long orderId) {
        List<OrderItemResponse> body = orderService.findItemsByOrderId(orderId);
        return ResponseEntity.ok(body);
    }
    @GetMapping("/orders/{orderId}/items/{itemId}")
    public ResponseEntity<OrderItemResponse> getOrderItem(@PathVariable Long orderId, @PathVariable Long itemId) {
        OrderItemResponse body = orderService.getOrderItem(orderId,itemId);
        return ResponseEntity.ok(body);
    }
}
