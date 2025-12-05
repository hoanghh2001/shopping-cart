package hoang.shop.cart.repository;

import hoang.shop.cart.model.CartItem;
import hoang.shop.common.enums.status.CartItemStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CartItemRepository extends JpaRepository<CartItem,Long> {
    Optional<CartItem> findByCartIdAndProductVariantId(Long cardId, Long productVariantId);

    Optional<CartItem> findByCartUserIdAndId(Long userId, Long cartItemId);


    List<CartItem> findByCartIdAndStatus(Long cartId, CartItemStatus status);

    Optional<CartItem>  findByCartIdAndId(Long cartId,Long cartItemId);
}
