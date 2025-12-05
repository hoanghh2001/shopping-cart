package hoang.shop.order.model;

import jakarta.persistence.*;
import lombok.*;
import hoang.shop.categories.model.ProductVariant;
import hoang.shop.common.baseEntity.BaseEntity;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;

@Entity
@Table(name = "order_items")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class OrderItem extends BaseEntity {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id",foreignKey = @ForeignKey(name = "fk_order_items_orders"),nullable = false)
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "variant_id",foreignKey = @ForeignKey(name = "fk_order_items_product_variants"),nullable = false)
    private ProductVariant productVariant;

    private String sku;

    @Column(nullable = false)
    private Integer quantity;

    @Column(name = "unit_price_before",nullable = false,precision = 12,scale = 2)
    private BigDecimal unitPriceBefore;

    @Column(name = "unit_price_at_order",nullable = false,precision = 12,scale = 2)
    private BigDecimal unitPriceAtOrder;
    @Builder.Default
    @Column(name = "line_discount",precision = 12,scale = 2)
    private BigDecimal lineDiscount= BigDecimal.ZERO;

    @Column(name = "line_total",precision = 12,scale = 2)
    private BigDecimal lineTotal;


}
