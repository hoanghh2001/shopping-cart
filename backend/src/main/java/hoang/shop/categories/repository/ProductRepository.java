package hoang.shop.categories.repository;

import hoang.shop.categories.model.Category;
import hoang.shop.categories.model.Product;
import hoang.shop.common.enums.ProductStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product,Long> , JpaSpecificationExecutor<Product> {



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

    Optional<Product> findBySlugAndStatus(String slug, ProductStatus status);







}
