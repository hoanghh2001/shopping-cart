package hoang.shop.cart.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import hoang.shop.categories.model.Product;
import hoang.shop.categories.model.ProductVariant;
import hoang.shop.common.baseEntity.BaseEntity;
import hoang.shop.common.enums.status.CartItemStatus;

import java.math.BigDecimal;

@Entity
@Table(name = "cart_items")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class CartItem  extends BaseEntity {

    @Version private Long version;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cart_id", foreignKey = @ForeignKey(name = "fk_cart_items_carts"))
    private Cart cart;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_variant_id",foreignKey = @ForeignKey(name = "fk_cart_items_product_variants"))
    private ProductVariant productVariant;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id",foreignKey = @ForeignKey(name = "fk_cart_items_products"))
    private Product product;

    @Column(nullable = false)
    private String productVariantName;

    @Column(length = 50)
    private String productVariantColor;

    @Column(length = 50)
    private String productVariantSize;

    @Column(nullable = false)
    private Integer quantity = 0;
    @Column(name = "unit_price_before",nullable = false)
    private BigDecimal unitPriceBefore = BigDecimal.ZERO;
    @Column(name = "unit_price")
    private BigDecimal unitPrice = BigDecimal.ZERO;
    @Column(name = "line_total")
    private BigDecimal lineTotal = BigDecimal.ZERO;
    private CartItemStatus status = CartItemStatus.ACTIVE;
    @Column(name = "image_url")
    private String imageUrl;


}
