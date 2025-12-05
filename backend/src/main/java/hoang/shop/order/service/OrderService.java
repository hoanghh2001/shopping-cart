package hoang.shop.order.service;

import hoang.shop.cart.dto.request.CheckoutRequest;
import hoang.shop.common.enums.OrderStatus;
import hoang.shop.order.dto.request.OrderSearchCondition;
import hoang.shop.order.dto.request.OrderUpdateRequest;
import hoang.shop.order.dto.response.OrderItemResponse;
import hoang.shop.order.dto.response.OrderResponse;
import hoang.shop.order.dto.response.OrderStatusHistoryResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

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
    List<OrderItemResponse> findItemsByOrderId(Long orderId);
    OrderItemResponse getOrderItem(Long orderId, Long itemId);

    //User
    OrderResponse createOrderFromCart(Long userId, CheckoutRequest request);
    OrderResponse updateOrderByUser(Long userId,Long orderId, OrderUpdateRequest request);
    OrderResponse cancelOrderByUser(Long userId, Long orderId, String reason);
    OrderResponse getOrderDetailForUser(Long userId, Long orderId);
    List<OrderResponse> findOrdersOfUser(Long userId);
    List<OrderResponse> findOrdersOfUserByStatus(Long userId, OrderStatus status);
    OrderItemResponse getOrderItemForUser(Long userId, Long orderItemId);
    Slice<OrderItemResponse> findItemsOfOrderForUser(Long userId, Long orderId,Pageable pageable);
    List<OrderStatusHistoryResponse> findStatusHistoryByOrderIdForUser(Long userId, Long orderId);


}
