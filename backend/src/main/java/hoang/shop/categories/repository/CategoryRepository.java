package hoang.shop.categories.repository;

import hoang.shop.categories.model.Category;
import hoang.shop.common.enums.CategoryStatus;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category,Long> {
    boolean existsByName(String name);
    boolean existsBySlug(String slug);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
            UPDATE Category c
                SET c.status = :status
            WHERE c.id = :id
                AND c.status <> :status
            
            """)
    int updateStatusById(@Param("id") Long id, @Param("status") CategoryStatus status);

    Category findByName(String name);
    Category findBySlug(String slug);

    @Query("""
            SELECT c
            FROM Category c
            JOIN c.products p
            WHERE p.id = :id
            """)
    Slice<Category> findByProductId(@Param("id") Long id, Pageable pageable);

    @Query("""
            SELECT c
            FROM Category c
            LEFT JOIN c.products p
            WHERE c.id = :id
            """)
    Optional<Category> findWithProductsById(@Param("id") Long id);
    Optional<Category> findBySlugAndStatus(String slug, CategoryStatus categoryStatus);
    Optional<Category> findByIdAndStatus(Long brandId, CategoryStatus categoryStatus);
    Slice<Category> findAllByStatus(CategoryStatus status, Pageable pageable);

}
