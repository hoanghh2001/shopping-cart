package hoang.shop.cart.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import hoang.shop.common.baseEntity.BaseEntity;
import hoang.shop.common.enums.CartStatus;
import hoang.shop.identity.model.User;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;


@Entity
@Table(name = "carts")
@Getter @Setter @SuperBuilder
@NoArgsConstructor @AllArgsConstructor
public class Cart extends BaseEntity {
    @Version private Long version;

    @OneToMany(mappedBy = "cart",cascade = CascadeType.ALL,orphanRemoval = true)
    @JsonIgnore
    @Builder.Default
    private List<CartItem> cartItems = new ArrayList<>();


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne(optional = false,fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id",nullable = false,
            foreignKey = @ForeignKey(name = "fk_carts_users"))
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private CartStatus status = CartStatus.ACTIVE;
    @Column(name = "total_quantity",nullable = false)
    @Builder.Default
    private Integer totalQuantity = 0;
    @Column(name = "total_amount",nullable = false, precision = 15, scale = 2)
    @Builder.Default
    private BigDecimal totalAmount  = BigDecimal.ZERO;

    public void recalculateTotals() {

        this.totalAmount = cartItems.stream()
                .map(CartItem::getLineTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, RoundingMode.HALF_UP);
    }

}
