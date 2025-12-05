package hoang.shop.categories.repository;

import hoang.shop.categories.model.ProductTag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProductTagRepository extends JpaRepository<ProductTag,Long> {
    @Modifying
    @Query(value = """
            INSERT IGNORE INTO product_tags (product_id, tag_id)
            VALUES (:pid, :tid)
            """, nativeQuery = true)
    void insertIfNotExists(@Param("pid") Long productId, @Param("tid") Long tagId);

    @Modifying
    @Query(value = """
            DELETE FROM ProductTag pt
            WHERE pt.product.id = :productId
                AND pt.tag.id IN :ids
            """)
    void deleteByProductAndTagIds(@Param("productId") Long productId,
                                  @Param("ids") List<Long> ids);


    Optional<ProductTag> findFirstByProduct_IdAndMainTrue(Long productId);
}
