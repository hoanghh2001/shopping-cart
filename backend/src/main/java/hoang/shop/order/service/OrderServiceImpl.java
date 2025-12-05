package hoang.shop.order.service;

import lombok.RequiredArgsConstructor;
import hoang.shop.common.enums.status.OrderStatus;
import hoang.shop.order.dto.request.OrderCreateRequest;
import hoang.shop.order.dto.request.OrderSearchCondition;
import hoang.shop.order.dto.request.OrderUpdateRequest;
import hoang.shop.order.dto.response.OrderItemResponse;
import hoang.shop.order.dto.response.OrderResponse;
import hoang.shop.order.dto.response.OrderStatusHistoryResponse;
import hoang.shop.order.mapper.OrderItemMapper;
import hoang.shop.order.mapper.OrderMapper;
import hoang.shop.order.mapper.OrderStatusHistoryMapper;
import hoang.shop.order.repository.OrderItemRepository;
import hoang.shop.order.repository.OrderRepository;
import hoang.shop.order.repository.OrderStatusHistoryRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
@Service
@Transactional
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService{
    private final OrderRepository orderRepository;
    private final OrderItemRepository itemRepository;
    private final OrderStatusHistoryRepository historyRepository;
    private final OrderMapper orderMapper;
    private final OrderItemMapper itemMapper;
    private final OrderStatusHistoryMapper historyMapper;

    @Override
    public Page<OrderResponse> findOrders(OrderSearchCondition condition, Pageable pageable) {
        Page<OrderResponse> orders =orderRepository.findByFilter(condition,pageable);
        return null;
    }

    @Override
    public OrderResponse getOrder(Long orderId) {
        return null;
    }

    @Override
    public OrderResponse confirmOrder(Long orderId, Long adminId) {
        return null;
    }

    @Override
    public OrderResponse markAsPacking(Long orderId, Long adminId) {
        return null;
    }

    @Override
    public OrderResponse markAsShipping(Long orderId, Long adminId) {
        return null;
    }

    @Override
    public OrderResponse markAsDelivered(Long orderId, Long adminId) {
        return null;
    }

    @Override
    public OrderResponse cancelOrderByAdmin(Long orderId, Long adminId, String reason) {
        return null;
    }

    @Override
    public List<OrderStatusHistoryResponse> findStatusHistoryByOrderId(Long orderId) {
        return List.of();
    }

    @Override
    public OrderStatusHistoryResponse getStatusHistoryRecord(Long historyId) {
        return null;
    }

    @Override
    public List<OrderItemResponse> findItemsByOrderId(Long orderId) {
        return List.of();
    }

    @Override
    public OrderItemResponse getOrderItem(Long orderItemId) {
        return null;
    }





    @Override
    public OrderResponse createOrderFromCart(Long userId, OrderCreateRequest request) {
        return null;
    }

    @Override
    public OrderResponse updateOrderByUser(Long userId, Long orderId, OrderUpdateRequest request) {
        return null;
    }

    @Override
    public OrderResponse cancelOrderByUser(Long userId, Long orderId, String reason) {
        return null;
    }

    @Override
    public OrderResponse getOrderDetailForUser(Long userId, Long orderId) {
        return null;
    }

    @Override
    public List<OrderResponse> findOrdersOfUser(Long userId) {
        return List.of();
    }

    @Override
    public List<OrderResponse> findOrdersOfUserByStatus(Long userId, OrderStatus status) {
        return List.of();
    }
}
