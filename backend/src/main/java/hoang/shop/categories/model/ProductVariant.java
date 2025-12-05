package hoang.shop.categories.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import hoang.shop.cart.model.CartItem;
import hoang.shop.common.baseEntity.BaseEntity;
import hoang.shop.common.enums.status.ProductVariantStatus;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "product_variants",indexes = {
        @Index(name = "idx_product_id",columnList = "product_id"),
        @Index(name = "idx_color_size",columnList = "color,size")
})
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class ProductVariant extends BaseEntity {
    //Constrain
    @OneToMany(mappedBy = "productVariant",cascade = CascadeType.ALL,orphanRemoval = true,fetch = FetchType.LAZY)
    @OrderColumn(name = "sort_order")
    private List<ProductVariantImage> productVariantImages = new ArrayList<>();

    @OneToMany(mappedBy = "productVariant",cascade = CascadeType.ALL,orphanRemoval = true,fetch = FetchType.LAZY)
    private List<CartItem> cartItems = new ArrayList<>();

    //Field
    @Version private Long version;

    @ManyToOne(fetch = FetchType.LAZY,optional = false)
    @JoinColumn(name = "product_id",nullable = false,
            foreignKey = @ForeignKey(name = "fk_product_variant_product"))
    private Product product;
    @Column(name = "variant_name", nullable = false,length = 150)
    private String name;
    @Column(length = 100)
    private String color;
    @Column(length = 50)
    private String size;
    @Column(nullable = false,precision = 10,scale = 2)
    private BigDecimal price;
    @Column(name = "compare_at_price",precision = 10,scale = 2,nullable = false)
    private BigDecimal compareAtPrice;
    @Column(name = "stock_quantity",nullable = false)
    private Integer stockQuantity = 0;
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ProductVariantStatus status = ProductVariantStatus.ACTIVE;

    public void addImage(ProductVariantImage productVariantImage){
        if (productVariantImage==null) return;
        productVariantImages.add(productVariantImage);
        productVariantImage.setProductVariant(this);
    }
    public void removeImage(ProductVariantImage productVariantImage){
        if (productVariantImage ==null) return;
        productVariantImages.remove(productVariantImage);
        productVariantImage.setProductVariant(null);
    }

}
