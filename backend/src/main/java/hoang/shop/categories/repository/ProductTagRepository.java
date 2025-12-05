package hoang.shop.categories.repository;

import hoang.shop.categories.model.Product;
import hoang.shop.categories.model.ProductTag;
import hoang.shop.categories.model.Tag;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

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
                AND pt.tag.id IN :distinctIds
            """)
    void deleteByProductAndTagIds(@Param("productId") Long productId,@Param("distinctIds") List<Long> distinctIds);
    @Query("""
            SELECT DISTINCT pt.tag
            FROM ProductTag pt
            WHERE pt.product.id = :productId
            """)
    Slice<Tag> findTagsByProductId(@Param("productId") Long productId, Pageable pageable);
    @Query("""
            SELECT DISTINCT pt.product
            FROM ProductTag pt
            WHERE pt.tag.id = :tagId
            """)
    Slice<Product> findProductsByTagId(@Param("tagId") Long tagId, Pageable pageable);
}
