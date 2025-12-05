package hoang.shop.categories.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import hoang.shop.cart.model.CartItem;
import hoang.shop.common.baseEntity.BaseEntity;
import hoang.shop.common.enums.ProductVariantStatus;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "product_variants",indexes = {
        @Index(name = "idx_product_variants_color_id",columnList = "product_color_id"),
        @Index(name = "idx_color_size",columnList = "color,size")}
        ,uniqueConstraints = {
        @UniqueConstraint(
                name = "uk_product_variants_color_size",
                columnNames = {"product_color_id", "size"}
        )}
)
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class ProductVariant extends BaseEntity {
    //Constrain

    @OneToMany(mappedBy = "productVariant",cascade = CascadeType.ALL,orphanRemoval = true,fetch = FetchType.LAZY)
    private List<CartItem> cartItems = new ArrayList<>();

    //Field
    @Version private Long version;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(nullable = false)
    private String sku;

    @ManyToOne(optional = false,fetch = FetchType.LAZY)
    @JoinColumn(name = "product_color_id",nullable = false,
            foreignKey = @ForeignKey(name = "fk_product_variants_product_colors"))
    private ProductColor color;

    @Column(length = 50)
    private String size;

    @Column(name = "regular_price",precision = 10,scale = 2,nullable = false)
    private BigDecimal regularPrice;

    @Column(name = "sale_price", precision = 10,scale = 2)
    private BigDecimal salePrice;

    @Column(nullable = false)
    private Integer stock = 0;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ProductVariantStatus status = ProductVariantStatus.ACTIVE;



}
