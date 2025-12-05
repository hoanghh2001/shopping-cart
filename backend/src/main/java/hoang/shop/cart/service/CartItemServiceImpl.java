package hoang.shop.cart.service;

import hoang.shop.cart.dto.response.ItemResponse;
import hoang.shop.categories.model.ProductColor;
import hoang.shop.categories.model.ProductColorImage;
import hoang.shop.categories.repository.ProductColorRepository;
import hoang.shop.categories.repository.ProductVariantRepository;
import hoang.shop.common.enums.CartItemStatus;
import hoang.shop.common.enums.CartStatus;
import hoang.shop.common.enums.ProductStatus;
import hoang.shop.common.enums.ProductVariantStatus;
import lombok.RequiredArgsConstructor;
import hoang.shop.cart.dto.request.CartItemCreateRequest;
import hoang.shop.cart.dto.request.CartItemUpdateRequest;
import hoang.shop.cart.mapper.CartItemMapper;
import hoang.shop.cart.model.Cart;
import hoang.shop.cart.model.CartItem;
import hoang.shop.cart.repository.CartItemRepository;
import hoang.shop.cart.repository.CartRepository;
import hoang.shop.categories.model.Product;
import hoang.shop.categories.model.ProductVariant;
import hoang.shop.categories.repository.ProductRepository;
import hoang.shop.common.exception.BadRequestException;
import hoang.shop.common.exception.NotFoundException;
import hoang.shop.identity.model.User;
import hoang.shop.identity.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;


@Service
@Transactional
@RequiredArgsConstructor
public class CartItemServiceImpl implements CartItemService{
    private final CartItemRepository cartItemRepository;
    private final CartRepository cartRepository;
    private final CartItemMapper cartItemMapper;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final ProductColorRepository colorRepository;
    private final ProductVariantRepository variantRepository;


    @Override
    public List<ItemResponse> findAllByCartId(Long cartId) {
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(()-> new NotFoundException("{error.cart.id.not-found}"));
        userRepository.findById(cart.getUser().getId())
                .orElseThrow(()-> new NotFoundException("{error.cart.id.not-found}"));
        List<CartItem> cartItem = cartItemRepository.findByCart_IdAndStatus(cartId, CartItemStatus.ACTIVE);

        return cartItem.stream().map(cartItemMapper::toResponse).collect(Collectors.toList());
    }

    @Override
    public ItemResponse create(Long userId, CartItemCreateRequest request) {
        if (request == null || request.variantId() == null
                || request.quantity() == null || request.quantity() < 1) {
            throw new BadRequestException("{error.cart-item.quantity.invalid}");
        }
        Cart cart = cartRepository.findByUserIdAndStatus(userId, CartStatus.ACTIVE)
                .orElseGet(() -> createEmptyCart(userId));
        cartRepository.save(cart);
        User user = userRepository.findById(userId)
                .orElseThrow(()-> new NotFoundException("{error.user.id.not-found}"));
        Product product = productRepository.findByIdAndStatus(request.variantId(), ProductStatus.ACTIVE)
                .orElseThrow(()-> new NotFoundException("{error.product.id.not-found}"));
        ProductVariant variant = variantRepository.findByIdAndStatus(request.variantId(), ProductVariantStatus.ACTIVE)
                .orElseThrow(()-> new NotFoundException("{error.product-variant.id.not-found}"));
        ProductColor color = variant.getColor();
        CartItem cartItem = cartItemRepository
                .findByCart_IdAndProductVariant_Id(cart.getId(), color.getId()).orElse(null);
        BigDecimal unitPriceAtOrder = variant.getSalePrice() == null ? variant.getRegularPrice()
                :variant.getSalePrice();
        BigDecimal unitPriceBefore = variant.getRegularPrice();
        String productName = color.getName();
        String size  = variant.getSize();
        String defaultImage = color.getImages().stream()
                .filter(ProductColorImage::isMain).toString();



        if (cartItem != null) {
            int newQty = cartItem.getQuantity() + request.quantity();
            if (variant.getStock() != null && newQty > variant.getStock()) {
                throw new BadRequestException("{error.cart-item.out-of-stock}");
            }
            cartItem.setNameLabel(productName);
            cartItem.setHexLabel(color.getHex());
            cartItem.setSizeLabel(size);
            cartItem.setQuantity(newQty);
            cartItem.setUnitPriceAtOrder(unitPriceAtOrder);
            cartItem.setUnitPriceBefore(unitPriceBefore);
            cartItem.setLineTotal(unitPriceAtOrder.multiply(BigDecimal.valueOf(newQty)));
        } else {
            if (variant.getStock() != null && request.quantity() > variant.getStock()) {
                throw new BadRequestException("{error.cart-item.out-of-stock}");
            }
             cartItem =  CartItem.builder()
                     .cart(cart)
                     .productVariant(variant)
                     .hexLabel(color.getHex())
                     .colorLabel(color.getName())
                     .nameLabel(color.getName())
                     .sizeLabel(variant.getSize())
                     .unitPriceBefore(variant.getRegularPrice())
                     .unitPriceAtOrder(variant.getSalePrice())
                     .quantity(request.quantity())
                     .lineTotal(variant.getSalePrice().multiply(BigDecimal.valueOf(request.quantity())))
                     .build();
             if (!color.getVariants().isEmpty())
                 cartItem.setImageUrl(defaultImage);
        }
        cartItem = cartItemRepository.saveAndFlush(cartItem);
        return cartItemMapper.toResponse(cartItem);
    }

    @Override
    public ItemResponse update(Long userId, Long cartItemId, CartItemUpdateRequest request) {
        if (request.quantity()<=0)
            throw new BadRequestException("{error.cart-item.quantity.bad-request}");

        Cart cart = cartRepository.findByUserIdAndStatus(userId,CartStatus.ACTIVE)
                .orElseThrow(()-> new NotFoundException("{error.cart.not-found}"));
        CartItem cartItem = cartItemRepository.findByCart_IdAndId(cart.getId(),cartItemId)
                 .orElseThrow(()-> new NotFoundException("{error.cart-item.not-found}"));
        ProductVariant productVariant = cartItem.getProductVariant();
        if (request.quantity() > cartItem.getProductVariant().getStock()) {
            throw new BadRequestException("{error.cart-item.quantity.exceeds-stock}");
        }
        cartItem.setQuantity(request.quantity());
        BigDecimal before = productVariant.getSalePrice();
        BigDecimal quantity = BigDecimal.valueOf(request.quantity());
        BigDecimal unitPrice = productVariant.getSalePrice()!= null ?
                productVariant.getSalePrice() : productVariant.getRegularPrice();
        BigDecimal total = unitPrice.multiply(quantity);
        cartItem.setQuantity(request.quantity());
        cartItem.setUnitPriceBefore(before);
        cartItem.setUnitPriceAtOrder(unitPrice);
        cartItem.setLineTotal(total);

        cartItemRepository.saveAndFlush(cartItem);
        cartRepository.save(cart);
        return cartItemMapper.toResponse(cartItem);
    }

    @Override
    public boolean delete(Long userId, Long cartItemId) {
        Cart cart = cartRepository.findByUserIdAndStatus(userId,CartStatus.ACTIVE)
                .orElseThrow(()-> new NotFoundException("{error.cart.not-found}"));
        CartItem cartItem = cartItemRepository.findByCart_IdAndId(cart.getId(),cartItemId)
                .orElseThrow(()-> new NotFoundException("{error.cart-item.not-found}"));
        cart.getCartItems().removeIf(i -> i.getId().equals(cartItemId));
        cartRepository.save(cart);
        return true;
    }

    @Override
    public List<ItemResponse> findAllByUserId(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(()-> new NotFoundException("{error.user.id.not-found}"));

        List<CartItem> items = cartItemRepository.findByCart_User_Id(userId);
        return items.stream().map(cartItemMapper::toResponse).toList();
    }


    private Cart createEmptyCart(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(()-> new NotFoundException("{error.user.id.not-found}"));
        return Cart.builder()
                .user(user)
                .status(CartStatus.ACTIVE)
                .build();
    }

}
