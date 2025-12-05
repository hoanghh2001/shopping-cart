package hoang.shop.cart.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import hoang.shop.categories.model.ProductVariant;
import hoang.shop.common.baseEntity.BaseEntity;
import hoang.shop.common.enums.CartItemStatus;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Entity
@Table(name = "cart_items",
    uniqueConstraints =
        @UniqueConstraint(name = "uk_cart_items_cart_variant",columnNames = "cart_id,variant_id"))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class CartItem  extends BaseEntity {

    @Version private Long version;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cart_id",nullable = false, foreignKey = @ForeignKey(name = "fk_cart_items_carts"))
    private Cart cart;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "variant_id",nullable = false,foreignKey = @ForeignKey(name = "fk_cart_items_product_variants"))
    private ProductVariant productVariant;

    @Column(nullable = false)
    private String nameLabel;

    @Column(nullable = false)
    private String sizeLabel;

    @Column(nullable = false)
    private String colorLabel;

    @Column(nullable = false)
    private String hexLabel;

    @Column(nullable = false)
    @Builder.Default
    private Integer quantity = 0;
    @Column(name = "unit_price_before", nullable = false)
    @Builder.Default
    private BigDecimal unitPriceBefore = BigDecimal.ZERO;
    @Column(name = "unit_price_at_order", nullable = false)
    @Builder.Default
    private BigDecimal unitPriceAtOrder = BigDecimal.ZERO;
    @Column(name = "line_total", nullable = false)
    @Builder.Default
    private BigDecimal lineTotal = BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private CartItemStatus status = CartItemStatus.ACTIVE;
    @Column(name = "image_url")
    private String imageUrl;
    public void recalculateLineTotals(){
        if (unitPriceAtOrder == null) unitPriceAtOrder = unitPriceBefore;
        if (quantity == null) quantity = 1;

        this.lineTotal = unitPriceAtOrder
                .multiply(BigDecimal.valueOf(quantity))
                .setScale(2, RoundingMode.HALF_UP);
    }



}
