package hoang.shop.order.repository;

import hoang.shop.order.model.OrderStatusHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderStatusHistoryRepository extends JpaRepository<OrderStatusHistory,Long> {
}
