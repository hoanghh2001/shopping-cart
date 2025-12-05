package hoang.shop.cart.service;

import hoang.shop.common.enums.status.CartItemStatus;
import hoang.shop.common.enums.status.CartStatus;
import hoang.shop.common.enums.status.ProductStatus;
import hoang.shop.common.enums.status.ProductVariantStatus;
import lombok.RequiredArgsConstructor;
import hoang.shop.cart.dto.request.CartItemCreateRequest;
import hoang.shop.cart.dto.request.CartItemUpdateRequest;
import hoang.shop.cart.dto.response.CartItemResponse;
import hoang.shop.cart.mapper.CartItemMapper;
import hoang.shop.cart.model.Cart;
import hoang.shop.cart.model.CartItem;
import hoang.shop.cart.repository.CartItemRepository;
import hoang.shop.cart.repository.CartRepository;
import hoang.shop.categories.model.Product;
import hoang.shop.categories.model.ProductVariant;
import hoang.shop.categories.repository.ProductRepository;
import hoang.shop.categories.repository.ProductVariantRepository;
import org.example.common.enums.status.*;
import hoang.shop.common.exception.BadRequestException;
import hoang.shop.common.exception.NotFoundException;
import hoang.shop.identity.model.User;
import hoang.shop.identity.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.Objects.requireNonNull;

@Service
@Transactional
@RequiredArgsConstructor
public class CartItemServiceImpl implements CartItemService{
    private final CartItemRepository cartItemRepository;
    private final CartRepository cartRepository;
    private final CartItemMapper cartItemMapper;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final ProductVariantRepository productVariantRepository;


    @Override
    public List<CartItemResponse> findAllByCartId(Long cartId) {
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(()-> new NotFoundException("{error.cart.id.not-found}"));
        userRepository.findById(cart.getUser().getId())
                .orElseThrow(()-> new NotFoundException("{error.cart.id.not-found}"));
        List<CartItem> cartItem = cartItemRepository.findByCartIdAndStatus(cartId, CartItemStatus.ACTIVE);

        return cartItem.stream().map(cartItemMapper::toResponse).collect(Collectors.toList());
    }

    @Override
    public CartItemResponse create(Long userId, CartItemCreateRequest request) {
        if (request == null || request.productVariantId() == null
                || request.quantity() == null || request.quantity() < 1) {
            throw new BadRequestException("{error.cart-item.quantity.invalid}");
        }
        Cart cart = cartRepository.findByUserIdAndStatus(userId, CartStatus.ACTIVE)
                .orElseGet(() -> createEmptyCart(userId));
        cartRepository.save(cart);
        User user = userRepository.findById(userId)
                .orElseThrow(()-> new NotFoundException("{error.user.id.not-found}"));
        Product product = productRepository.findByIdAndStatus(request.productVariantId(), ProductStatus.ACTIVE)
                .orElseThrow(()-> new NotFoundException("{error.product.id.not-found}"));
        ProductVariant productVariant = productVariantRepository.findByIdAndStatus(request.productVariantId(), ProductVariantStatus.ACTIVE)
                .orElseThrow(()-> new NotFoundException("{error.product-variant.id.not-found}"));
        CartItem cartItem = cartItemRepository
                .findByCartIdAndProductVariantId(cart.getId(), productVariant.getId())
                .orElse(null);
        BigDecimal unitPrice = productVariant.getPrice()!= null? productVariant.getPrice():productVariant.getCompareAtPrice();
        BigDecimal unitPriceBefore = productVariant.getCompareAtPrice();
        String variantName = productVariant.getName();
        String color = productVariant.getColor();
        String size  = productVariant.getSize();
        if (cartItem != null) {
            int newQty = cartItem.getQuantity() + request.quantity();

            if (productVariant.getStockQuantity() != null && newQty > productVariant.getStockQuantity()) {
                throw new BadRequestException("{error.cart-item.out-of-stock}");
            }
            cartItem.setProductVariantName(variantName);
            cartItem.setProductVariantColor(color);
            cartItem.setProductVariantSize(size);
            cartItem.setQuantity(newQty);
            cartItem.setUnitPrice(unitPrice);
            cartItem.setUnitPriceBefore(unitPriceBefore);
            cartItem.setLineTotal(unitPrice.multiply(BigDecimal.valueOf(newQty)));



        } else {
             cartItem =  CartItem.builder()
                     .cart(cart)
                     .product(productVariant.getProduct())
                     .productVariant(productVariant)
                     .productVariantName(productVariant.getName())
                     .productVariantColor(productVariant.getColor())
                     .productVariantSize(productVariant.getSize())
                     .unitPriceBefore(productVariant.getCompareAtPrice())
                     .unitPrice(productVariant.getPrice())
                     .quantity(request.quantity())
                     .lineTotal(productVariant.getPrice().multiply(BigDecimal.valueOf(request.quantity())))
                     .build();
             if (!productVariant.getProductVariantImages().isEmpty())
                 cartItem.setImageUrl(productVariant.getProductVariantImages().getFirst().toString());
            cartItemRepository.save(cartItem);
        }
        cartItemRepository.save(cartItem);
        recalc(cart);
        return cartItemMapper.toResponse(cartItem);
    }

    @Override
    public CartItemResponse update(Long userId, Long cartItemId, CartItemUpdateRequest request) {
        if (request.quantity()<=0)
            throw new BadRequestException("{error.cart-item.quantity.bad-request}");

        Cart cart = cartRepository.findByUserIdAndStatus(userId,CartStatus.ACTIVE)
                .orElseThrow(()-> new NotFoundException("{error.cart.not-found}"));
        CartItem cartItem = cartItemRepository.findByCartIdAndId(cart.getId(),cartItemId)
                 .orElseThrow(()-> new NotFoundException("{error.cart-item.not-found}"));
        ProductVariant productVariant = cartItem.getProductVariant();
        if (request.quantity() > cartItem.getProductVariant().getStockQuantity()) {
            throw new BadRequestException("{error.cart-item.quantity.exceeds-stock}");
        }
        cartItem.setQuantity(request.quantity());
        BigDecimal before = productVariant.getCompareAtPrice();
        BigDecimal quantity = BigDecimal.valueOf(request.quantity());
        BigDecimal unitPrice = productVariant.getPrice()!= null ? productVariant.getPrice() : productVariant.getCompareAtPrice();
        BigDecimal total = unitPrice.multiply(quantity);
        cartItem.setQuantity(request.quantity());
        cartItem.setUnitPriceBefore(before);
        cartItem.setUnitPrice(unitPrice);
        cartItem.setLineTotal(total);

        cartItemRepository.saveAndFlush(cartItem);
        recalc(cart);
        cartRepository.save(cart);
        return cartItemMapper.toResponse(cartItem);
    }

    @Override
    public boolean delete(Long userId, Long cartItemId) {
        Cart cart = cartRepository.findByUserIdAndStatus(userId,CartStatus.ACTIVE)
                .orElseThrow(()-> new NotFoundException("{error.cart.not-found}"));
        CartItem cartItem = cartItemRepository.findByCartIdAndId(cart.getId(),cartItemId)
                .orElseThrow(()-> new NotFoundException("{error.cart-item.not-found}"));
        cart.getCartItems().removeIf(i -> i.getId().equals(cartItemId));
        recalc(cart);
        cartRepository.save(cart);
        return true;
    }


    private Cart createEmptyCart(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(()-> new NotFoundException("{error.user.id.not-found}"));
        return Cart.builder()
                .user(user)
                .status(CartStatus.ACTIVE)
                .build();
    }



    private void recalc(Cart cart) {
        if (cart.getCartItems() == null || cart.getCartItems().isEmpty()) {
            cart.setSubtotalAmount(BigDecimal.ZERO);
            cart.setGrandTotal(BigDecimal.ZERO);
            return;
        }
        BigDecimal subtotal = cart.getCartItems().stream()
                .map(item -> item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal discount = defaultIfNull(cart.getDiscountAmount());
        BigDecimal shipping = defaultIfNull(cart.getShippingFee());
        BigDecimal tax = defaultIfNull(cart.getTaxAmount());
        BigDecimal grandTotal = subtotal.subtract(discount).add(shipping).add(tax);
        subtotal = subtotal.setScale(2, RoundingMode.HALF_UP);
        grandTotal = grandTotal.setScale(2, RoundingMode.HALF_UP);
        cart.setSubtotalAmount(subtotal);
        cart.setGrandTotal(grandTotal);
    }
    private BigDecimal defaultIfNull(BigDecimal value) {
        return value != null ? value : BigDecimal.ZERO;
    }
}
