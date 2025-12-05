package hoang.shop.order.repository;

import hoang.shop.order.model.OrderItem;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OrderItemRepository extends JpaRepository<OrderItem,Long> {
    List<OrderItem> findByOrder_Id(Long orderId);

    Optional<OrderItem> findByOrder_User_IdAndId(Long userId,Long orderItemId);


    Slice<OrderItem> findByOrderId(Long orderId, Pageable pageable);

}
