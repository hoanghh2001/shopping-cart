package hoang.shop.order.model;

import hoang.shop.common.enums.PaymentMethod;
import jakarta.persistence.*;
import lombok.*;
import hoang.shop.common.baseEntity.BaseEntity;
import hoang.shop.common.enums.OrderStatus;
import hoang.shop.common.enums.PaymentStatus;
import hoang.shop.identity.model.User;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
@Setter @Getter
@NoArgsConstructor @AllArgsConstructor
@SuperBuilder
public class Order extends BaseEntity {

    @OneToMany(mappedBy = "order",cascade = CascadeType.ALL,orphanRemoval = true)
    @Builder.Default
    private List<OrderItem> items = new ArrayList<>();


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "order_number")
    private String orderNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", foreignKey = @ForeignKey(name = "fk_orders_users"))
    private User user;

    @Column(name = "subtotal_amount",nullable = false,precision = 12,scale = 2)
    private BigDecimal subtotalAmount;

    @Column(name = "discount_amount",precision = 12,scale = 2)
    private BigDecimal discountAmount;

    @Column(name = "shipping_fee",nullable = false,precision = 12,scale = 2)
    private BigDecimal shippingFee;

    @Column(name = "tax_amount",precision = 12,scale = 2)
    private BigDecimal taxAmount;

    @Column(name = "grand_total",nullable = false,precision = 12,scale = 2)
    private BigDecimal grandTotal;

    @Column(name = "order_status")
    @Builder.Default
    private OrderStatus orderStatus = OrderStatus.PENDING;

    @Column(name = "payment_status")
    @Builder.Default
    private PaymentStatus paymentStatus = PaymentStatus.PENDING;

    @Column(name = "payment_method")
    @Builder.Default
    private PaymentMethod paymentMethod = PaymentMethod.CASH;

    @Column(nullable = false)
    private String name;

    @Column(name = "postal_code",nullable = false)
    private String postalCode;


    @Column(nullable = false)
    private String phone;

    @Column(name = "full_address",nullable = false)
    private String fullAddress;

    @Column(name = "placed_ad")
    private Instant placedAt;

    @Column(name = "paid_ad")
    private Instant paidAt;

    @Column(name = "shipped_at")
    private Instant shippedAt;

    @Column(name = "delivered_at")
    private Instant deliveredAt;

    private String reason;

    @Column(name = "cancelled_at")
    private Instant cancelledAt;

}
