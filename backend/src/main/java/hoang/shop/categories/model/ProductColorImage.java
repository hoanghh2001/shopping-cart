package hoang.shop.categories.model;


import hoang.shop.common.baseEntity.BaseEntity;
import hoang.shop.common.enums.ImageStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Entity
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Table(name = "product_color_images")
public class ProductColorImage extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false,fetch = FetchType.LAZY)
    @JoinColumn(name = "color_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_product_color_images_product_colors"))
    private ProductColor color;

    @Column(name = "sort_order",nullable = false)
    Integer sortOrder = 0;

    @Column(name = "image_url",nullable = false,length = 500)
    private String imageUrl;

    private Integer width;

    private Integer height;

    @Column(name = "mime_type")
    String mimeType;

    @Column(name = "size_bytes")
    Long sizeBytes;

    @Column(name = "is_main", nullable = false)
    private boolean main = false;

    @Column(nullable = false)
    private ImageStatus status = ImageStatus.ACTIVE;




}
