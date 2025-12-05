package hoang.shop.order.repository;

import hoang.shop.order.dto.request.OrderSearchCondition;
import hoang.shop.order.dto.response.OrderResponse;
import hoang.shop.order.model.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

public interface OrderRepository extends JpaRepository<Order,Long>, JpaSpecificationExecutor<Order> {

    @Query("""
            SELECT o
            FROM Order o
            WHERE OrderSearchCond
            """)
    Page<OrderResponse> findByFilter(OrderSearchCondition condition, Pageable pageable);
}