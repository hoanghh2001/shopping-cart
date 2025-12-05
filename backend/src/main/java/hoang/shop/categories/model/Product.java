package hoang.shop.categories.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import hoang.shop.common.baseEntity.BaseEntity;
import hoang.shop.common.enums.ProductStatus;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Table(
        name = "products",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_products_name",columnNames = "name")
        },
        indexes = {
                @Index(name = "ix_products_brand_id", columnList = "brand_id"),
                @Index(name = "ix_products_category_id", columnList = "category_id"),
                @Index(name = "ix_products_status", columnList = "status"),
                @Index(name = "ix_products_slug",columnList = "slug",unique = true),
                @Index(name = "idx_products_created_at",columnList = "created_at")
        }
)
public class Product extends BaseEntity {
    //constrain

    @OneToMany(mappedBy = "product",cascade = CascadeType.ALL,fetch = FetchType.LAZY)
    private List<ProductReview> reviews = new ArrayList<>();

    @OneToMany(mappedBy = "product",fetch = FetchType.LAZY)
    private List<ProductTag> tags = new ArrayList<>();

    @OneToMany(mappedBy = "product",cascade = CascadeType.ALL,orphanRemoval = true, fetch = FetchType.LAZY)
    private List<ProductColor> colors = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "brand_id",foreignKey = @ForeignKey(name = "fk_products_brand"))
    private Brand brand;

    @ManyToOne(optional = false,fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id",
            foreignKey = @ForeignKey(name = "fk_products_category"))
    private Category category;

    //field
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false,length = 255)
    private String name;
    @Column(length = 500)
    private String description;
    @Column(nullable = false)
    private String slug;
    @Column(name = "min_price")
    private BigDecimal minPrice;
    @Column(name = "max_price")
    private BigDecimal maxPrice;
    @Enumerated(EnumType.STRING)
    @Column( length = 20,nullable = false)
    private ProductStatus status = ProductStatus.ACTIVE;

}
