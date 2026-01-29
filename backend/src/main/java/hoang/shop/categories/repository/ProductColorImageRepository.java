package hoang.shop.categories.repository;

import aj.org.objectweb.asm.commons.Remapper;
import hoang.shop.categories.model.ProductColorImage;
import hoang.shop.common.enums.ColorFamily;
import hoang.shop.common.enums.status.ImageStatus;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.parameters.P;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface ProductColorImageRepository extends JpaRepository<ProductColorImage, Long> {
    @Query("""
            SELECT i
            FROM ProductColorImage i
            WHERE (:colorId IS NULL OR i.color.id = :colorId)
              AND (:status IS NULL OR i.status = :status)
              AND (:isMain IS NULL OR i.main = :isMain)
            """)
    List<ProductColorImage> search(@Param("colorId") Long colorId, @Param("status") ImageStatus status, @Param("isMain") Boolean isMain, Pageable pageable);

    List<ProductColorImage> findByIdInAndColor_IdAndStatus(List<Long> ids, Long colorId, ImageStatus status);

    Optional<ProductColorImage> findByColor_IdAndMainTrue(Long colorId);

    @Query("""
            select pci from ProductColorImage pci
            join pci.color pc
            where pc.product.id = :productId
              and pc.colorFamily = :color
              and pci.main = true
            """)
    Optional<ProductColorImage> findMainImageByProductAndColor(
            @Param("productId") Long productId,
            @Param("color") ColorFamily color
    );

    @Query("""
            select img
            from ProductColorImage img
            where img.main = true
            and img.color.product.id in :productIds
            and img.color.colorFamily = :family
            """)
    List<ProductColorImage> findMainImagesByProductIdsAndColorFamily(
            @Param("productIds") List<Long> productIds,
            @Param("family") ColorFamily family
    );
}

