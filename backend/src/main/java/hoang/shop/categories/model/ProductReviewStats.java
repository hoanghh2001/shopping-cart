package hoang.shop.categories.model;

import hoang.shop.common.baseEntity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "product_review_stats")
@Getter @Setter
@Builder
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor @AllArgsConstructor
public class ProductReviewStats  {

    @Id
    private Long productId;

    private BigDecimal averageRating = BigDecimal.ZERO;
    private long reviewCount = 0L;

    private long rating1 = 0L;
    private long rating2 = 0L;
    private long rating3 = 0L;
    private long rating4 = 0L;
    private long rating5 = 0L;

    @CreatedDate
    @Column(name ="created_at",updatable = false)
    private Instant createdAt = Instant.now();

    @CreatedBy
    @Column(name = "created_by")
    private Long createdBy;

    @LastModifiedDate
    @Column(name = "updated_at")
    private Instant updatedAt;

    @LastModifiedBy
    @Column(name = "updated_by")
    private Long updatedBy;

}
