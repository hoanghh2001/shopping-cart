package hoang.shop.order.model;

import jakarta.persistence.*;
import hoang.shop.common.enums.status.ChangedByType;
import hoang.shop.common.enums.status.OrderStatus;

import java.time.Instant;

@Entity
@Table(name = "order_status_histories")
public class OrderStatusHistory  {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id",foreignKey = @ForeignKey(name = "fk_order_status_histories_orders"))
    private Order order;
    @Enumerated(EnumType.STRING)
    @Column(name = "old_status",nullable = false)
    private OrderStatus oldStatus;
    @Enumerated(EnumType.STRING)
    @Column(name = "new_status",nullable = false)
    private OrderStatus newStatus;
    @Enumerated(EnumType.STRING)
    @Column(name = "changed_by_type")
    private ChangedByType changedByType;
    @Column(name = "changed_by_id")
    private Long changedById;
    @Column(length = 500)
    private String note;
    @Column(name = "changed_at")
    private Instant changedAt;
    @Column(name = "created_at")
    private Instant createdAt;
    @PrePersist
    public void prePersist() {
        Instant now = Instant.now();
        if (changedAt == null) changedAt = now;
        if (createdAt == null) createdAt = now;
    }











}
