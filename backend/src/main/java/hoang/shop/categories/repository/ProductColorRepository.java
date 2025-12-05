package hoang.shop.categories.repository;

import hoang.shop.categories.model.ProductColor;
import hoang.shop.common.enums.ProductColorStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProductColorRepository extends JpaRepository<ProductColor,Long> {
    List<ProductColor> findByProduct_Id(Long productId);

    List<ProductColor> findByProduct_SlugAndStatus(String productSlug, ProductColorStatus status);

    Optional<ProductColor> findFirstByProduct_IdAndMainTrue(Long productId);

}

