package hoang.shop.categories.repository;

import hoang.shop.categories.model.ProductColor;
import hoang.shop.common.enums.status.ProductColorStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface ProductColorRepository extends JpaRepository<ProductColor, Long> {
    List<ProductColor> findByProduct_Id(Long productId);

    List<ProductColor> findByProduct_SlugAndStatus(String productSlug, ProductColorStatus status);

    Optional<ProductColor> findFirstByProduct_IdAndMainTrue(Long productId);


    @Query("""
            select pc
            from ProductColor pc
            where pc.main = true and pc.product.id in :productIds
            """)
    List<ProductColor> findMainColorsByProductIds(@Param("productIds") List<Long> productIds);

}

