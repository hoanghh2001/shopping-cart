package hoang.shop.categories.model;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import hoang.shop.common.baseEntity.BaseEntity;
import hoang.shop.common.enums.TagStatus;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "tags",
        uniqueConstraints = @UniqueConstraint(name = "uk_tags_slug",columnNames = {"slug"}),
        indexes = {
                @Index(name = "ix_tags_name",columnList = "name"),
                @Index(name = "ix_tags_slug",columnList = "slug")}
)
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class Tag extends BaseEntity {
    //Constrain


    @OneToMany(mappedBy = "tag",cascade = CascadeType.ALL,orphanRemoval = true,fetch = FetchType.LAZY)
    private List<ProductTag> productTags = new ArrayList<>();

    

    //Field
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(nullable = false)
    private String name ;

    @Column(nullable = false)
    private String slug;

    @Column(length = 500)
    private String description;

    @Enumerated(EnumType.STRING)
    private TagStatus status = TagStatus.ACTIVE;


}
