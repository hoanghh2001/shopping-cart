package hoang.shop.order.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import hoang.shop.common.JapanAddress;
import hoang.shop.common.baseEntity.BaseEntity;
import hoang.shop.common.enums.status.OrderStatus;
import hoang.shop.common.enums.status.PaymentStatus;
import hoang.shop.identity.model.User;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
@Setter @Getter
@NoArgsConstructor @AllArgsConstructor
public class Order extends BaseEntity {

    @OneToMany(mappedBy = "order",cascade = CascadeType.ALL,orphanRemoval = true)
    private List<OrderItem> orderItems = new ArrayList<>();

    @Column(name = "order_number")
    private String orderNumber;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(foreignKey = @ForeignKey(name = "fk_orders_users"))
    private User user;
    @Embedded
    private JapanAddress japanAddress;
    @Column(name = "subtotal_amount",nullable = false,precision = 12,scale = 2)
    private BigDecimal subtotalAmount;
    @Column(name = "discount_amount",precision = 12,scale = 2)
    private BigDecimal discountAmount;
    @Column(name = "shipping_fee",nullable = false,precision = 12,scale = 2)
    private BigDecimal shippingFee;
    @Column(name = "tax_amount",nullable = false,precision = 5,scale = 2)
    private BigDecimal taxAmount;
    @Column(name = "grand_total",nullable = false,precision = 12,scale = 2)
    private BigDecimal grandTotal;
    private OrderStatus orderStatus = OrderStatus.PENDING;
    private PaymentStatus paymentStatus = PaymentStatus.PENDING;
    @Column(name = "placed_ad")
    private Instant placedAt;
    @Column(name = "paid_ad")
    private Instant paidAt;
    @Column(name = "shipped_at")
    private Instant shippedAt;
    @Column(name = "delivered_at")
    private Instant deliveredAt;
    @Column(name = "cancelled_at")
    private Instant cancelledAt;

}
