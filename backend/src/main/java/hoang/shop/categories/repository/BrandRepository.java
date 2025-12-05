package hoang.shop.categories.repository;

import hoang.shop.categories.model.Brand;
import hoang.shop.common.enums.BrandStatus;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface BrandRepository extends JpaRepository<Brand,Long> {

    Optional<Brand> findByName(String name);
    Optional<Brand> findBySlug(String slug);
    @Query("""
            SELECT b
            FROM Brand b
            WHERE :status IS NULL OR b.status = :status
            """)
    Slice<Brand> findByStatus(BrandStatus status, Pageable pageable);
    @Modifying(clearAutomatically = true,flushAutomatically = true)
    @Query("""
            UPDATE Brand b
            SET b.status = :status
            WHERE b.id = :id
            """)
    int updateStatusById(@Param("id") Long id, @Param("status") BrandStatus status);
    boolean existsByName(String name);
    boolean existsBySlug(String slug);
    boolean existsByNameAndIdNot(String name,Long id);
    boolean existsBySlugAndIdNot(String slug, Long id);

    Slice<Brand> findAllByStatus(BrandStatus brandStatus, Pageable pageable);

    Optional<Brand> findBySlugAndStatus(String slug, BrandStatus brandStatus);

    Optional<Brand> findByIdAndStatus(Long brandId, BrandStatus brandStatus);
}
