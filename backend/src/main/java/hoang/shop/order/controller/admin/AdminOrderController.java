package hoang.shop.order.controller.admin;

import hoang.shop.order.dto.request.OrderSearchCondition;
import hoang.shop.order.dto.response.OrderItemResponse;
import hoang.shop.order.dto.response.OrderResponse;
import hoang.shop.order.dto.response.OrderStatusHistoryResponse;
import hoang.shop.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;

    public Page<OrderResponse> findOrders(OrderSearchCondition condition, Pageable pageable) {
        return orderService.findOrders(condition, pageable);
    }

    public OrderResponse getOrder(Long orderId) {
        return orderService.getOrder(orderId);
    }

    public OrderResponse confirmOrder(Long orderId, Long adminId) {
        return orderService.confirmOrder(orderId, adminId);
    }

    public OrderResponse markAsPacking(Long orderId, Long adminId) {
        return orderService.markAsPacking(orderId, adminId);
    }

    public OrderResponse markAsShipping(Long orderId, Long adminId) {
        return orderService.markAsShipping(orderId, adminId);
    }

    public OrderResponse markAsDelivered(Long orderId, Long adminId) {
        return orderService.markAsDelivered(orderId, adminId);
    }

    public OrderResponse cancelOrderByAdmin(Long orderId, Long adminId, String reason) {
        return orderService.cancelOrderByAdmin(orderId, adminId, reason);
    }

    public List<OrderStatusHistoryResponse> findStatusHistoryByOrderId(Long orderId) {
        return orderService.findStatusHistoryByOrderId(orderId);
    }

    public List<OrderItemResponse> findItemsByOrderId(Long orderId) {
        return orderService.findItemsByOrderId(orderId);
    }

    public OrderItemResponse getOrderItem(Long orderItemId) {
        return orderService.getOrderItem(orderItemId);
    }
}
