package hoang.shop.cart.repository;

import hoang.shop.cart.model.CartItem;
import hoang.shop.common.enums.CartItemStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CartItemRepository extends JpaRepository<CartItem,Long> {
    List<CartItem> findByCart_Id(Long cartId);


    Optional<CartItem> findByCart_IdAndProductVariant_Id(Long cardId, Long variantId);

    Optional<CartItem> findByCart_User_IdAndId(Long userId, Long cartItemId);


    List<CartItem> findByCart_IdAndStatus(Long cartId, CartItemStatus status);

    Optional<CartItem> findByCart_IdAndId(Long cartId, Long cartItemId);

    List<CartItem> findByCart_User_Id(Long userId);

}
