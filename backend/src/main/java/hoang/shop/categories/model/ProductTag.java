package hoang.shop.categories.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Setter @Getter
@NoArgsConstructor @AllArgsConstructor
@Table(name = "product_tags",
        uniqueConstraints = @UniqueConstraint(name = "uk_product_tags_product_tag",columnNames = {"product_id","tag_id"})
)
public class ProductTag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    @ManyToOne(optional = false,fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id",foreignKey = @ForeignKey(name = "fk.product_tags.products"))
    Product product;
    @ManyToOne(optional = false,fetch = FetchType.LAZY)
    @JoinColumn(name = "tag_id",foreignKey = @ForeignKey(name = "fk.product_tags.tags"))
    Tag tag;



}
