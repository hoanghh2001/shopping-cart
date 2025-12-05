package hoang.shop.categories.model;


import hoang.shop.common.baseEntity.BaseEntity;
import hoang.shop.common.enums.ProductColorStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Table(
        name = "product_colors",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_product_color_name",
                        columnNames = {"product_id", "color_name"}
                ),
                @UniqueConstraint(
                        name = "uk_product_color_slug",
                        columnNames = {"product_id", "color_slug"}
                )
        }
)
public class ProductColor extends BaseEntity {

    @OneToMany(mappedBy = "color")
    private List<ProductColorImage> images = new ArrayList<>();

    @OneToMany(mappedBy = "color")
    private List<ProductVariant> variants = new ArrayList<>();

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false,fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id",nullable = false,
            foreignKey = @ForeignKey(name = "fk_product_colors_products"))
    private Product product;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "hex", length = 7)
    private String hex;

    @Column(name = "is_main", nullable = false)
    private boolean main = false;

    public ProductColorStatus status = ProductColorStatus.ACTIVE;

    public void addImages(List<ProductColorImage> images) {
        if (images == null || images.isEmpty()) return;

        for (ProductColorImage img : images) {
            if (img == null) continue;
            img.setColor(this);
            this.images.add(img);
        }
    }

    public void removeImage(ProductColorImage image) {
        images.remove(image);
        image.setColor(this);
    }



}
