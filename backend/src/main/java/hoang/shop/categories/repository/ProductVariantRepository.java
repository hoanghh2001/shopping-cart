package hoang.shop.categories.repository;

import hoang.shop.categories.model.ProductVariant;
import hoang.shop.common.enums.ProductVariantStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;

public interface ProductVariantRepository extends JpaRepository<ProductVariant,Long> , JpaSpecificationExecutor<ProductVariant> {

    Optional<ProductVariant> findByIdAndStatus(Long productVariantId, ProductVariantStatus status);


    List<ProductVariant> findByColor_Id(Long colorId);

}

