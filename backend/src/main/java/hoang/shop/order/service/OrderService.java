package hoang.shop.order.service;

import hoang.shop.common.enums.status.OrderStatus;
import hoang.shop.order.dto.request.OrderCreateRequest;
import hoang.shop.order.dto.request.OrderSearchCondition;
import hoang.shop.order.dto.request.OrderUpdateRequest;
import hoang.shop.order.dto.response.OrderItemResponse;
import hoang.shop.order.dto.response.OrderResponse;
import hoang.shop.order.dto.response.OrderStatusHistoryResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface OrderService {
    //Admin
    Page<OrderResponse> findOrders(OrderSearchCondition condition, Pageable pageable);
    OrderResponse getOrder(Long orderId);
    OrderResponse confirmOrder(Long orderId, Long adminId);
    OrderResponse markAsPacking(Long orderId, Long adminId);
    OrderResponse markAsShipping(Long orderId, Long adminId);
    OrderResponse markAsDelivered(Long orderId, Long adminId);
    OrderResponse cancelOrderByAdmin(Long orderId, Long adminId, String reason);
    List<OrderStatusHistoryResponse> findStatusHistoryByOrderId(Long orderId);
    OrderStatusHistoryResponse getStatusHistoryRecord(Long historyId);
    List<OrderItemResponse> findItemsByOrderId(Long orderId);
    OrderItemResponse getOrderItem(Long orderItemId);

    //User
    OrderResponse createOrderFromCart(Long userId,OrderCreateRequest request);
    OrderResponse updateOrderByUser(Long userId,Long orderId, OrderUpdateRequest request);
    OrderResponse cancelOrderByUser(Long userId, Long orderId, String reason);
    OrderResponse getOrderDetailForUser(Long userId, Long orderId);
    List<OrderResponse> findOrdersOfUser(Long userId);
    List<OrderResponse> findOrdersOfUserByStatus(Long userId, OrderStatus status);
}
