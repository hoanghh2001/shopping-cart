package hoang.shop.categories.repository;

import hoang.shop.categories.dto.response.PriceTuple;
import hoang.shop.categories.model.ProductVariant;
import hoang.shop.common.enums.status.ProductVariantStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface ProductVariantRepository extends JpaRepository<ProductVariant, Long>, JpaSpecificationExecutor<ProductVariant> {

    Optional<ProductVariant> findByIdAndStatus(Long productVariantId, ProductVariantStatus status);


    List<ProductVariant> findByColor_Id(Long colorId);

    @Query("""
            select distinct v.color.product.id
            from ProductVariant v
            where v.color.product.id in :productIds
            and v.stock > 0
            """)
    List<Long> findInStockProductIds(@Param("productIds") List<Long> productIds);

    @Query("""
            select new hoang.shop.categories.dto.response.PriceTuple(
              v.color.id,
              min(v.regularPrice),
              min(v.salePrice)
            )
            from ProductVariant v
            where v.color.id in :colorIds
            group by v.color.id
            """)
    List<PriceTuple> findMinPricesByColorIds(@Param("colorIds") List<Long> colorIds);

}

