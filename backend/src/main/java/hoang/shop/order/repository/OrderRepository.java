package hoang.shop.order.repository;

import hoang.shop.common.enums.OrderStatus;
import hoang.shop.order.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order,Long>, JpaSpecificationExecutor<Order> {

    Optional<Order> findByUserIdAndId(Long userId, Long orderId);

    List<Order> findAllByUser_Id(Long userId);

    List<Order> findAllByUserIdAndOrderStatus(Long userId, OrderStatus orderStatus);
}