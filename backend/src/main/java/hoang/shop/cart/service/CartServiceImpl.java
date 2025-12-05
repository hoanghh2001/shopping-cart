package hoang.shop.cart.service;

import hoang.shop.cart.dto.request.CheckoutRequest;
import hoang.shop.categories.model.ProductColorImage;
import hoang.shop.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import hoang.shop.cart.dto.request.CartItemCreateRequest;
import hoang.shop.cart.dto.request.CartItemUpdateRequest;
import hoang.shop.cart.dto.response.CartResponse;
import hoang.shop.cart.mapper.CartItemMapper;
import hoang.shop.cart.mapper.CartMapper;
import hoang.shop.cart.model.Cart;
import hoang.shop.cart.model.CartItem;
import hoang.shop.cart.repository.CartItemRepository;
import hoang.shop.cart.repository.CartRepository;
import hoang.shop.categories.model.Product;
import hoang.shop.categories.model.ProductVariant;
import hoang.shop.categories.repository.ProductVariantRepository;
import hoang.shop.common.enums.CartStatus;
import hoang.shop.common.exception.BadRequestException;
import hoang.shop.common.exception.NotFoundException;
import hoang.shop.identity.model.User;
import hoang.shop.identity.repository.UserRepository;
import hoang.shop.order.dto.response.OrderResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class CartServiceImpl implements CartService{
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductVariantRepository productVariantRepository;
    private final CartMapper cartMapper;
    private final CartItemMapper cartItemMapper;
    private final UserRepository userRepository;
    private final OrderService orderService;


    @Override
    public CartResponse getMyCart(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(()-> new NotFoundException("error.user.id.not-found"));
        Cart cart = cartRepository.findByUserIdAndStatus(userId, CartStatus.ACTIVE)
                .orElseGet(()-> createEmptyCart(userId));
        return cartMapper.toResponse(cart);
    }
    @Override
    @Transactional
    public CartResponse addItem(Long userId, CartItemCreateRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(()-> new NotFoundException("error.user.id.not-found"));

        Cart cart = cartRepository.findByUserIdAndStatus(userId, CartStatus.ACTIVE)
                .orElseGet(() -> createEmptyCart(userId));

        ProductVariant variant = productVariantRepository.findById(request.variantId())
                .orElseThrow(() -> new NotFoundException("{error.product-variant.id.not-found}"));

        if (request.quantity() == null || request.quantity() <= 0) {
            throw new BadRequestException("{error.cart-item.quantity.invalid}");
        }


        List<ProductColorImage> images = variant.getColor().getImages();
        String imageUrl = null;
        if (images != null && !images.isEmpty()) {
            imageUrl = images.stream()
                    .filter(ProductColorImage::isMain)
                    .map(ProductColorImage::getImageUrl)
                    .findFirst()
                    .orElse("/uploads/default/no-image.png");
        }

        BigDecimal unitPriceAtOrder = variant.getSalePrice()!=null?
                variant.getSalePrice():variant.getRegularPrice();

        CartItem item = cartItemRepository
                .findByCart_IdAndProductVariant_Id(cart.getId(), variant.getId())
                .orElse(null);
        if (item==null) {
            BigDecimal lineTotal = unitPriceAtOrder.multiply(BigDecimal.valueOf(request.quantity()));
            CartItem newItem = CartItem.builder()
                    .cart(cart)
                    .productVariant(variant)
                    .sizeLabel(variant.getSize())
                    .nameLabel(variant.getColor().getProduct().getName())
                    .colorLabel(variant.getColor().getName())
                    .hexLabel(variant.getColor().getHex())
                    .quantity(request.quantity())
                    .unitPriceAtOrder(unitPriceAtOrder)
                    .unitPriceBefore(variant.getRegularPrice())
                    .lineTotal(lineTotal)
                    .imageUrl(imageUrl)
                    .build();
            cart.getCartItems().add(newItem);
        }else {
            int oldLineTotalQuantity = item.getQuantity();
            int freshLineTotalQuantity = oldLineTotalQuantity+request.quantity();
            item.setUnitPriceAtOrder(unitPriceAtOrder);
            item.setUnitPriceBefore(variant.getRegularPrice());
            item.setQuantity(freshLineTotalQuantity);
            item.recalculateLineTotals();

        }
        // list số lượng sản phẩm có trong item
        List<Integer> items =  cart.getCartItems().stream().map(CartItem::getQuantity).toList();
        // tổng số sản phẩm có trên 1 dòng -> lúc chưa thêm vào
        int oldTotalQuantity = items.stream().reduce(0,Integer::sum);
        // tổng số sản phẩm -> sau khi thêm
        int freshTotalQuantity = oldTotalQuantity+ request.quantity();
        cart.setTotalQuantity(freshTotalQuantity);
        cart.recalculateTotals();
        cartRepository.save(cart);
        return cartMapper.toResponse(cart);
    }


    @Override
    public CartResponse updateItem(Long userId, Long itemId, CartItemUpdateRequest request) {
        return null;
    }

    @Override
    public void removeItem(Long userId, Long itemId) {
        Cart cart = cartRepository.findByUserIdAndStatus(userId, CartStatus.ACTIVE)
                .orElseThrow(() -> new NotFoundException("error.cart-active.user-id.not-found"));
        CartItem cartItem = cartItemRepository.findByCart_User_IdAndId(userId,itemId)
                .orElseThrow(()-> new NotFoundException("error.item.id.not-found"));
        cartItemRepository.delete(cartItem);
        cart.recalculateTotals();

    }

    @Override
    public void clearItems(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(()-> new NotFoundException("error.user.id.not-found"));
        Cart cart = cartRepository.findByUserIdAndStatus(userId, CartStatus.ACTIVE)
                .orElseGet(() -> createEmptyCart(userId));
        if (cart.getCartItems() != null) {
            cart.getCartItems().clear();
        }
        cart.setTotalAmount(BigDecimal.ZERO);
        cartRepository.save(cart);
    }

    @Override
    public OrderResponse checkout(Long userId, CheckoutRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(()-> new NotFoundException("error.user.id.not-found"));

        Cart cart = cartRepository.findByUserIdAndStatus(userId, CartStatus.ACTIVE)
                .orElseThrow(() -> new NotFoundException("error.card.user-id.not-found"));
        return orderService.createOrderFromCart(user.getId(), request);
    }



    private Cart createEmptyCart(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(()-> new NotFoundException("{error.user.id.not-found}"));
        return Cart.builder()
                .user(user)
                .build();
    }


}
