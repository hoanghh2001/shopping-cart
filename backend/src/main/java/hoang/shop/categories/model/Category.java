package hoang.shop.categories.model;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import hoang.shop.common.baseEntity.BaseEntity;
import hoang.shop.common.enums.CategoryStatus;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table( name = "categories",
        uniqueConstraints ={ @UniqueConstraint(name = "uk_categories_slug",columnNames = "slug"),
                            @UniqueConstraint(name = "uk_categories_name",columnNames = "name")},
        indexes = {
                @Index(name = "uk_categories_parent_slug", columnList = "parent_id,slug", unique = true),
                @Index(name = "uk_categories_parent_slug", columnList = "parent_id,slug", unique = true)
        }
)
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class Category extends BaseEntity {

    //constraint
    @OneToMany(mappedBy = "category",cascade = CascadeType.PERSIST)
    private List<Product> products = new ArrayList<>();

    @OneToMany(mappedBy = "parent",cascade = CascadeType.PERSIST)
    private List<Category> children = new ArrayList<>() ;


    //field
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false,length = 100)
    private String name;

    @Column(nullable = false,length = 255)
    private String slug;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id"
            ,foreignKey = @ForeignKey(name = "fk_category_parent"),nullable = true)
    private Category parent;

    @Column(name = "image_url",length = 500)
    private String imageUrl;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CategoryStatus status = CategoryStatus.ACTIVE;

    public void addProducts(List<Product> productsToAdd) {
        if (productsToAdd == null || productsToAdd.isEmpty()) return;
        for (Product p : productsToAdd) {
            if (!products.contains(p)) {
                products.add(p);
                p.setCategory(this);
            }
        }
    }
    public void removeProducts(List<Product> productsToRemove) {
        if (productsToRemove == null || productsToRemove.isEmpty()) return;
        for (Product p : productsToRemove) {
            if (products.contains(p)) {
                products.remove(p);
                p.setCategory(null);
            }
        }
    }
    public void addChildren(Category child) {
        children.add(child);
        child.setParent(this);
    }

    public void removeChildren(Category child) {
        children.remove(child);
        child.setParent(null);
    }


}
