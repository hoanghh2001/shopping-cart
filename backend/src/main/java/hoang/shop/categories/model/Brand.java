package hoang.shop.categories.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import hoang.shop.common.baseEntity.BaseEntity;
import hoang.shop.common.enums.status.BrandStatus;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "brands",
        indexes = @Index(name = "uk_brands_slug",columnList = "slug",unique = true)
)
@Setter @Getter
@NoArgsConstructor @AllArgsConstructor
public class Brand extends BaseEntity {

    @OneToMany(mappedBy = "brand", cascade = CascadeType.ALL,orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Product> products= new ArrayList<>();


    //Field
    @Column(nullable = false)
    private String name;
    @Column(nullable = false)
    private String slug;
    @Column(length = 255)
    private String description;
    @Column(name = "logo_url",length = 500)
    private String logoUrl;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false,length = 20)
    private BrandStatus status = BrandStatus.ACTIVE;

    public void addProduct(Product product){
        products.add(product);
        product.setBrand(this);
    }
    public void removeProduct(Product product){
        products.remove(product);
        product.setBrand(null);
    }


}
