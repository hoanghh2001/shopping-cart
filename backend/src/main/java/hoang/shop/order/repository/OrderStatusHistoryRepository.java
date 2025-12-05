package hoang.shop.order.repository;

import hoang.shop.order.model.OrderStatusHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderStatusHistoryRepository extends JpaRepository<OrderStatusHistory,Long> {


    List<OrderStatusHistory> findByOrderId(Long orderId);

    List<OrderStatusHistory> findByOrder_User_IdAndOrder_Id(Long userId, Long orderId);
}
