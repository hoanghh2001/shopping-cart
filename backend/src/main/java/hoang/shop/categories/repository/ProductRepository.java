package hoang.shop.categories.repository;

import hoang.shop.categories.model.Category;
import hoang.shop.categories.model.Product;
import hoang.shop.common.enums.status.ProductStatus;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product,Long> {
    @Query("""
            SELECT p
            FROM Product p
            WHERE   (:status IS NULL OR p.status = :status)
                AND (:brandId IS NULL OR p.brand.id = :brandId)
                AND (:categoryId IS NULL OR p.category.id = :categoryId)
                AND (:minPrice IS NULL OR p.price>= :minPrice)
                AND (:maxPrice IS NULL OR p.price<= :maxPrice)
                AND(:keyword IS NULL
                    OR LOWER(p.name) LIKE LOWER(CONCAT('%',:keyword,'%'))
                    OR LOWER(p.slug) LIKE LOWER(CONCAT('%',:keyword,'%'))
                    OR LOWER(p.description) LIKE LOWER(CONCAT('%',:keyword,'%'))
                    )
            """)
    Slice<Product> findByFilter(
            @Param("keyword") String keyword,
            @Param("status") ProductStatus status,
            @Param("brandId") Long brandId,
            @Param("categoryId") Long categoryId,
            @Param("minPrice") BigDecimal minPrice,
            @Param("maxPrice") BigDecimal maxPrice,
            Pageable pageable
    );
    @Query("""
            SELECT p
            FROM Product p
            WHERE p.brand.id = :id
                AND p.status = ACTIVE
            """)
    Slice<Product> findByBrandId(@Param("id")Long id,Pageable pageable);
    @Query("""
            SELECT p
            FROM Product p
            WHERE p.category.id = :id
                AND p.status = ACTIVE
            """)
    Slice<Product> findByCategoryId(@Param("id")Long id, Pageable pageable);
    boolean existsByName(String name);
    boolean existsBySlug(String slug);
    boolean existsByNameAndIdNot( String name,Long id);
    boolean existsBySlugAndIdNot( String slug,Long id);
    @Modifying(flushAutomatically = true,clearAutomatically = true)
    @Query("""
            UPDATE Product p
            SET p.status = :status
            WHERE p.id IN :ids
            """)
    int updateStatusById(@Param("ids") List<Long> ids, @Param("status") ProductStatus status);
    Optional<Product> findByName(String name);
    Optional<Product> findBySlug(String slug);
    @Query("""
            SELECT p
            FROM Product p
            WHERE p.id = :id
            """)
    Optional<Product> findById(@Param("id") Long id);

    @Modifying(clearAutomatically = true,flushAutomatically = true)
    @Query("""
            UPDATE Product p
            SET p.category = :category
            WHERE p.id IN :ids
            """)
    int bulkAssignToCategory(@Param("category") Category category,@Param("ids")List<Long> ids );
    @Modifying(clearAutomatically = true,flushAutomatically = true)
    @Query("""
            UPDATE Product p
            SET p.category = null
            WHERE p.category.id = :categoryId
                AND p.id IN :ids
            """)
    int bulkRemoveFromCategory(@Param("categoryId") Long categoryId, @Param("ids") List<Long> ids );
    @Query("""
            SELECT COUNT(p)
            FROM Product p
            WHERE p.id IN :ids
            """)
    int countByIdIn(@Param("ids") List<Long> ids);

    Optional<Product> findByIdAndStatus(Long id, ProductStatus status);
}
