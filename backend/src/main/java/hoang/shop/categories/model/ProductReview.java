package hoang.shop.categories.model;

import hoang.shop.common.baseEntity.BaseEntity;
import hoang.shop.common.enums.status.ProductReviewStatus;
import hoang.shop.common.exception.BadRequestException;
import hoang.shop.identity.model.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(
        name = "product_reviews",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_product_reviews_product_user",
                        columnNames = {"product_id", "user_id"}
                )
        },
        indexes = {
                @Index(name = "idx_product_reviews_product_id", columnList = "product_id"),
                @Index(name = "idx_product_reviews_user_id", columnList = "user_id"),
                @Index(name = "idx_product_reviews_product_rating", columnList = "product_id, rating")
        }
)
public class ProductReview extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "product_id")
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id")
    private User user;

    private int rating;

    private String title;
    @Column(name = "image_url",length = 500)
    String imageUrl;

    @Column(columnDefinition = "TEXT")
    private String content;

    private ProductReviewStatus status = ProductReviewStatus.ACTIVE;

}
