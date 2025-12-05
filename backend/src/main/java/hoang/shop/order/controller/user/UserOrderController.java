package hoang.shop.order.controller.user;

import hoang.shop.common.enums.status.OrderStatus;
import hoang.shop.order.dto.request.OrderCreateRequest;
import hoang.shop.order.dto.request.OrderUpdateRequest;
import hoang.shop.order.dto.response.OrderResponse;
import hoang.shop.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user/{userId}")
@RequiredArgsConstructor
public class UserOrderController {
    private final OrderService orderService;
    @PostMapping
    public OrderResponse createOrderFromCart(Long userId, OrderCreateRequest request) {
        return orderService.createOrderFromCart(userId, request);
    }
    @PatchMapping("")
    public OrderResponse updateOrderByUser(Long userId, Long orderId, OrderUpdateRequest request) {
        return orderService.updateOrderByUser(userId, orderId, request);
    }
    @PatchMapping("")
    public OrderResponse cancelOrderByUser(Long userId, Long orderId, String reason) {
        return orderService.cancelOrderByUser(userId, orderId, reason);
    }
    @GetMapping
    public OrderResponse getOrderDetailForUser(Long userId, Long orderId) {
        return orderService.getOrderDetailForUser(userId, orderId);
    }
    @GetMapping
    public List<OrderResponse> findOrdersOfUser(Long userId) {
        return orderService.findOrdersOfUser(userId);
    }
    @GetMapping
    public List<OrderResponse> findOrdersOfUserByStatus(Long userId, OrderStatus status) {
        return orderService.findOrdersOfUserByStatus(userId, status);
    }
}
