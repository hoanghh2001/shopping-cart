package hoang.shop.order.service;

import hoang.shop.cart.dto.request.CheckoutRequest;
import hoang.shop.cart.model.Cart;
import hoang.shop.cart.model.CartItem;
import hoang.shop.cart.repository.CartItemRepository;
import hoang.shop.cart.repository.CartRepository;
import hoang.shop.common.enums.CartStatus;
import hoang.shop.common.exception.BadRequestException;
import hoang.shop.common.exception.NotFoundException;
import hoang.shop.identity.model.Address;
import hoang.shop.identity.model.User;
import hoang.shop.identity.repository.AddressRepository;
import hoang.shop.identity.repository.UserRepository;
import hoang.shop.order.model.Order;
import hoang.shop.order.model.OrderItem;
import hoang.shop.order.model.OrderStatusHistory;
import hoang.shop.order.spec.OrderSpec;
import lombok.RequiredArgsConstructor;
import hoang.shop.common.enums.OrderStatus;
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
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService{
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final OrderStatusHistoryRepository historyRepository;
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final UserRepository userRepository;
    private final AddressRepository addressRepository;
    private final OrderMapper orderMapper;
    private final OrderItemMapper itemMapper;
    private final OrderStatusHistoryMapper historyMapper;

    @Override
    public Page<OrderResponse> findOrders(OrderSearchCondition condition, Pageable pageable) {
        Specification<Order> spec = OrderSpec.build(condition);
        Page<Order> page =orderRepository.findAll(spec,pageable);
        return page.map(orderMapper::toResponse);
    }

    @Override
    public OrderResponse getOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new NotFoundException("error.order.id.not-found"));
        return orderMapper.toResponse(order);
    }

    @Override
    public OrderResponse confirmOrder(Long orderId, Long adminId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new NotFoundException("error.order.id.not-found"));
        OrderStatus oldStatus = order.getOrderStatus();
        if (order.getOrderStatus() != OrderStatus.PENDING) {
            throw new BadRequestException("error.order.status.invalid-confirm");
        }
        order.setUpdatedBy(adminId);
        order.setOrderStatus(OrderStatus.CONFIRMED);
        historyRepository.save(
                OrderStatusHistory.builder()
                        .order(order)
                        .oldStatus(oldStatus)
                        .newStatus(OrderStatus.CONFIRMED)
                        .changedById(adminId)
                        .changedAt(Instant.now())
                        .build()
        );
        return orderMapper.toResponse(order);
    }

    @Override
    public OrderResponse markAsPacking(Long orderId, Long adminId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new NotFoundException("error.order.id.not-found"));
        OrderStatus oldStatus = order.getOrderStatus();
        if (order.getOrderStatus() != OrderStatus.CONFIRMED) {
            throw new BadRequestException("error.order.status.invalid-confirm");
        }
        order.setUpdatedBy(adminId);
        order.setOrderStatus(OrderStatus.PACKING);
        historyRepository.save(
                OrderStatusHistory.builder()
                        .order(order)
                        .oldStatus(oldStatus)
                        .newStatus(OrderStatus.PACKING)
                        .changedById(adminId)
                        .changedAt(Instant.now())
                        .build()
        );
        return orderMapper.toResponse(order);

    }

    @Override
    public OrderResponse markAsShipping(Long orderId, Long adminId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new NotFoundException("error.order.id.not-found"));
        OrderStatus oldStatus = order.getOrderStatus();
        if (order.getOrderStatus() != OrderStatus.PACKING) {
            throw new BadRequestException("error.order.status.invalid-confirm");
        }
        order.setUpdatedBy(adminId);
        order.setOrderStatus(OrderStatus.SHIPPING);
        historyRepository.save(
                OrderStatusHistory.builder()
                        .order(order)
                        .oldStatus(oldStatus)
                        .newStatus(OrderStatus.SHIPPING)
                        .changedById(adminId)
                        .changedAt(Instant.now())
                        .build()
        );
        return orderMapper.toResponse(order);
    }

    @Override
    public OrderResponse markAsDelivered(Long orderId, Long adminId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new NotFoundException("error.order.id.not-found"));
        OrderStatus oldStatus = order.getOrderStatus();

        if (order.getOrderStatus() != OrderStatus.SHIPPING) {
            throw new BadRequestException("error.order.status.invalid-confirm");
        }
        order.setUpdatedBy(adminId);
        order.setOrderStatus(OrderStatus.DELIVERED);
        historyRepository.save(
                OrderStatusHistory.builder()
                        .order(order)
                        .oldStatus(oldStatus)
                        .newStatus(OrderStatus.DELIVERED)
                        .changedById(adminId)
                        .changedAt(Instant.now())
                        .build()
        );
        return orderMapper.toResponse(order);
    }

    @Override
    public OrderResponse cancelOrderByAdmin(Long orderId, Long adminId, String reason) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new NotFoundException("error.order.id.not-found"));
        OrderStatus oldStatus = order.getOrderStatus();

        if (order.getOrderStatus() == OrderStatus.DELIVERED
                || order.getOrderStatus() == OrderStatus.CANCELLED)
            throw new BadRequestException("error.order.status.invalid-cancel");
        order.setUpdatedBy(adminId);
        order.setOrderStatus(OrderStatus.CANCELLED);
        order.setReason(reason);
        historyRepository.save(
                OrderStatusHistory.builder()
                        .order(order)
                        .oldStatus(oldStatus)
                        .newStatus(OrderStatus.CANCELLED)
                        .changedById(adminId)
                        .changedAt(Instant.now())
                        .build()
        );
        return orderMapper.toResponse(order);
    }

    @Override
    public List<OrderStatusHistoryResponse> findStatusHistoryByOrderId(Long orderId) {
        List<OrderStatusHistory> list = historyRepository.findByOrderId(orderId);
        if (list == null || list.isEmpty()) {
            return List.of();
        }
        return list.stream().map(historyMapper::toResponse).toList();
    }

    @Override
    public List<OrderItemResponse> findItemsByOrderId(Long orderId) {
        List<OrderItem> list = orderItemRepository.findByOrder_Id(orderId);
        return list.stream().map(itemMapper::toResponse).toList();
    }

    @Override
    public OrderItemResponse getOrderItem(Long orderId, Long itemId) {
        OrderItem orderItem = orderItemRepository.findByOrder_User_IdAndId(orderId,itemId)
                .orElseThrow(()-> new NotFoundException("{error.order-item.id.not-found}"));
        return itemMapper.toResponse(orderItem);
    }



    @Override
    public OrderResponse createOrderFromCart(Long userId, CheckoutRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("{error.user.id.not-found}"));

        Cart cart = cartRepository.findByUserIdAndStatus(userId, CartStatus.ACTIVE)
                .orElseThrow(() -> new NotFoundException("{error.cart-active.user-id.not-found}"));
        Address address;
        if (request.addressId() == null) {
            address = addressRepository.findDefaultAddressByUserId(userId)
                    .orElseThrow(() -> new NotFoundException("{error.address.default.not-found}"));
        } else{
            address = addressRepository.findByIdAndUser_Id(request.addressId(), userId)
                .orElseThrow(() -> new NotFoundException("{error.address.id.not-found}"));
        }

        List<CartItem> cartItems = cartItemRepository.findByCart_Id(cart.getId());
        if (cartItems.isEmpty()) {
            throw new BadRequestException("{error.cart.empty}");
        }
        BigDecimal subtotalAmount = BigDecimal.ZERO;
        BigDecimal discountAmount = BigDecimal.ZERO;
        BigDecimal shippingFee;
        BigDecimal taxRate = BigDecimal.valueOf(0.10);

        List<OrderItem> orderItems = new ArrayList<>();

        for (CartItem cartItem : cartItems) {
            BigDecimal price = cartItem.getProductVariant().getSalePrice();
            BigDecimal lineTotal = price.multiply(BigDecimal.valueOf(cartItem.getQuantity()));

            subtotalAmount = subtotalAmount.add(lineTotal);

            OrderItem orderItem = OrderItem.builder()
                    .productVariant(cartItem.getProductVariant())
                    .sku("")
                    .quantity(cartItem.getQuantity())
                    .unitPriceBefore(cartItem.getProductVariant().getRegularPrice())
                    .unitPriceAtOrder(price)
                    .lineTotal(lineTotal)
                    .build();

            orderItems.add(orderItem);
        }
        if (subtotalAmount.compareTo(BigDecimal.valueOf(10000)) > 0) {
            shippingFee = BigDecimal.ZERO;
        }else {
            shippingFee = BigDecimal.valueOf(500);
        }

        BigDecimal taxableBase = subtotalAmount
                .subtract(discountAmount)
                .add(shippingFee);

        BigDecimal taxAmount = taxableBase.multiply(taxRate);
        BigDecimal grandTotal = taxableBase.add(taxAmount);
        String name = request.name().isBlank() ? address.getName() : request.name();

        Order order = Order.builder()
                .user(user)
                .name(name)
                .phone(address.getPhone())
                .postalCode(address.getPostalCode())
                .fullAddress(address.getFullAddress())
                .paymentMethod(request.paymentMethod())
                .subtotalAmount(subtotalAmount)
                .discountAmount(discountAmount)
                .shippingFee(shippingFee)
                .taxAmount(taxAmount)
                .grandTotal(grandTotal)
                .items(new ArrayList<>())
                .build();

        for (OrderItem item : orderItems) {
            item.setOrder(order);
            order.getItems().add(item);
        }

        Order saved = orderRepository.save(order);
        cartRepository.delete(cart);
        return orderMapper.toResponse(saved);
    }

    @Override
    public OrderResponse updateOrderByUser(Long userId, Long orderId, OrderUpdateRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(()-> new NotFoundException("{error.user.id.not-found}"));
        Order order = orderRepository.findByUserIdAndId(userId,orderId)
                .orElseThrow(()-> new NotFoundException("{error.order.id.not-found}"));
        Address address = addressRepository.findById(request.newAddressId())
                .orElseThrow(()-> new NotFoundException("{error.address.id.not-found}"));
        if (order.getOrderStatus()==OrderStatus.CANCELLED)
            throw new BadRequestException("error.order.status.invalid-cancel");
        if (order.getOrderStatus()==OrderStatus.SHIPPING)
            throw new BadRequestException("error.order.status.invalid-shipping");
        if (order.getOrderStatus()==OrderStatus.DELIVERED)
            throw new BadRequestException("error.order.status.invalid-delivered");
        order.setName(address.getName());
        order.setPhone(address.getPhone());
        order.setPostalCode(address.getPostalCode());
        order.setFullAddress(address.getFullAddress());
        return orderMapper.toResponse(order);
    }

    @Override
    public OrderResponse cancelOrderByUser(Long userId, Long orderId, String reason) {
        User user = userRepository.findById(userId)
                .orElseThrow(()-> new NotFoundException("{error.user.id.not-found}"));
        Order order = orderRepository.findByUserIdAndId(userId,orderId)
                .orElseThrow(()-> new NotFoundException("{error.order.id.not-found}"));
        if (order.getOrderStatus()==OrderStatus.CANCELLED)
            throw new BadRequestException("error.order.status.invalid-cancel");
        if (order.getOrderStatus()==OrderStatus.SHIPPING)
            throw new BadRequestException("error.order.status.invalid-shipping");
        if (order.getOrderStatus()==OrderStatus.DELIVERED)
            throw new BadRequestException("error.order.status.invalid-delivered");
        order.setReason(reason);
        order.setOrderStatus(OrderStatus.CANCELLED);
        order.setCancelledAt(Instant.now());
        OrderStatus oldStatus = order.getOrderStatus();
        historyRepository.save(
                OrderStatusHistory.builder()
                        .order(order)
                        .oldStatus(oldStatus)
                        .newStatus(OrderStatus.CANCELLED)
                        .changedById(userId)
                        .changedAt(Instant.now())
                        .build()
        );
        return orderMapper.toResponse(order);
    }

    @Override
    public OrderResponse getOrderDetailForUser(Long userId, Long orderId) {
        User user = userRepository.findById(userId)
                .orElseThrow(()-> new NotFoundException("{error.user.id.not-found}"));
        Order order = orderRepository.findByUserIdAndId(userId,orderId)
                .orElseThrow(()-> new NotFoundException("{error.order.id.not-found}"));
        return orderMapper.toResponse(order);
    }

    @Override
    public List<OrderResponse> findOrdersOfUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(()-> new NotFoundException("{error.user.id.not-found}"));
        List<Order> orders = orderRepository.findAllByUser_Id(userId);
        return orders.stream().map(orderMapper::toResponse).toList();
    }

    @Override
    public List<OrderResponse> findOrdersOfUserByStatus(Long userId, OrderStatus status) {
        User user = userRepository.findById(userId)
                .orElseThrow(()-> new NotFoundException("{error.user.id.not-found}"));
        List<Order> orders = orderRepository.findAllByUserIdAndOrderStatus(userId,status);
        return orders.stream().map(orderMapper::toResponse).toList();
    }

    @Override
    public OrderItemResponse getOrderItemForUser(Long userId, Long orderItemId) {
        User user = userRepository.findById(userId)
                .orElseThrow(()-> new NotFoundException("{error.user.id.not-found}"));
        OrderItem orderItem = orderItemRepository.findByOrder_User_IdAndId(userId,orderItemId)
                .orElseThrow(()-> new NotFoundException("{error.order-item.id.not-found}"));

        return itemMapper.toResponse(orderItem);
    }

    @Override
    public Slice<OrderItemResponse> findItemsOfOrderForUser(Long userId, Long orderId,Pageable pageable) {
        User user = userRepository.findById(userId)
                .orElseThrow(()-> new NotFoundException("{error.user.id.not-found}"));
        Slice<OrderItem> items = orderItemRepository.findByOrderId(orderId, pageable);
        return items.map(itemMapper::toResponse);
    }

    @Override
    public List<OrderStatusHistoryResponse> findStatusHistoryByOrderIdForUser(Long userId, Long orderId) {
        User user = userRepository.findById(userId)
                .orElseThrow(()-> new NotFoundException("{error.user.id.not-found}"));
        List<OrderStatusHistory> histories = historyRepository.findByOrder_User_IdAndOrder_Id(userId,orderId);
        return List.of();
    }
}
