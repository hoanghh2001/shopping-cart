package hoang.shop.categories.repository;

import hoang.shop.common.enums.status.ProductVariantImageStatus;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProductVariantImageRepository extends JpaRepository<ProductVariantImage,Long> {
    @Modifying
    @Query("""
            UPDATE ProductVariantImage pvi
            SET pvi.status = :status
            WHERE pvi.id IN :ids
            """)
    int updateStatusById(@Param("status") ProductVariantImageStatus status,@Param("ids") List<Long> ids);
    boolean existsByProductVariantIdAndIsMainTrue(Long id);
    @Query("""
            SELECT pvi
            FROM ProductVariantImage pvi
            WHERE pvi.productVariant.id = :variantId
                AND (:isMain IS NULL OR pvi.isMain = :isMain)
                AND (:status IS NULL OR pvi.status = :status)
            """)
    List<ProductVariantImage> findByProductVariantIdAndIsMainAndStatus(
            @Param("variantId") Long variantId,
            @Param("isMain") Boolean isMain,
            @Param("status")ProductVariantImageStatus status,
            Pageable pageable);

    Optional<ProductVariantImage> findByIdAndProductVariant_Id(Long imageId, Long variantId);

    List<ProductVariantImage> findByProductVariant_IdAndIdIn(Long variantId, List<Long> ids);

    Optional<ProductVariantImage> findByProductVariant_IdAndId(Long variantId, Long imageId);

    List<ProductVariantImage> findByProductVariant_IdAndStatus(Long variantId, ProductVariantImageStatus status);
}
