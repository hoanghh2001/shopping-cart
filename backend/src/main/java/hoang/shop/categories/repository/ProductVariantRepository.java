package hoang.shop.categories.repository;

import hoang.shop.categories.model.ProductVariant;
import hoang.shop.common.enums.status.ProductVariantStatus;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface ProductVariantRepository extends JpaRepository<ProductVariant,Long> {

    @Query("""
            SELECT pv
            FROM ProductVariant pv
            WHERE pv.product.id = :id
                AND pv.status = :status
            """)
    Slice<ProductVariant> findByProductId(
            @Param("id") Long id,
            @Param("status") ProductVariantStatus status,
            Pageable pageable);

    @Query("""
            SELECT pv FROM ProductVariant pv
            WHERE    (:color IS NULL OR LOWER(pv.color) = LOWER(:color))
                 AND (:size IS NULL OR LOWER(pv.size) = LOWER(:size))
                 AND (:status IS NULL OR pv.status = :status)
                 AND (:id IS NULL OR pv.product.id = :id)
                 AND (:min IS NULL OR pv.price >= :min)
                 AND (:max IS NULL OR pv.price <= :max)
                 AND (:keyword IS NULL
                 OR LOWER(CONCAT(pv.name, ' ', pv.color, ' ', pv.size))
                 LIKE LOWER(CONCAT('%', :keyword, '%')))
            """)
    Slice   <ProductVariant> findByFilter(
            @Param("keyword") String keyword,
            @Param("color") String color,
            @Param("size") String size,
            @Param("status") ProductVariantStatus status,
            @Param("id") Long productId,
            @Param("min") BigDecimal minPrice,
            @Param("max") BigDecimal maxPrice,
            Pageable pageable
    );
    @Query("""
            UPDATE ProductVariant pv
            SET pv.status = :status
            WHERE pv.id = :id
            """)
    int updateStatusById(@Param("status") ProductVariantStatus status,@Param("id") Long id);
    boolean existsByName(String name);
    boolean existsByColorAndSize(String color, String size);
    boolean existsByNameAndIdNot(String name,Long id);
    boolean existsByColorAndSizeAndIdNot(String color,String size,Long id);

    List<ProductVariant> findByProductId(Long productId);

    Optional<ProductVariant> findByIdAndStatus(Long productVariantId, ProductVariantStatus status);
}

