package hoang.shop.cart.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import hoang.shop.common.baseEntity.BaseEntity;
import hoang.shop.common.enums.status.CartStatus;
import hoang.shop.identity.model.User;

import java.math.BigDecimal;
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
    private List<CartItem> cartItems = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id",foreignKey = @ForeignKey(name = "fk_carts_users"))
    private User user;
    @Column(name = "session_id")
    private String sessionId;
    @Enumerated(EnumType.STRING)
    private CartStatus status;

    @Column(name = "subtotal_amount", precision = 15, scale = 2)
    private BigDecimal subtotalAmount;
    @Column(name = "discount_amount", precision = 15, scale = 2)
    private BigDecimal discountAmount;
    @Column(name = "shipping_fee", precision = 15, scale = 2)
    private BigDecimal shippingFee;
    @Column(name = "tax_amount", precision = 15, scale = 2)
    private BigDecimal taxAmount;
    @Column(name = "grand_total", precision = 15, scale = 2)
    private BigDecimal grandTotal;



}
