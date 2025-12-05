package hoang.shop.cart.repository;

import hoang.shop.cart.model.Cart;
import hoang.shop.common.enums.status.CartStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart,Long> {

    Optional<Cart> findByUserIdAndStatus(Long userId, CartStatus status);
}
