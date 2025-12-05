package hoang.shop.identity.repository;

import hoang.shop.identity.model.Address;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
public interface AddressRepository extends JpaRepository<Address,Long> {

    List<Address> findAllByUserId(Long userId);

    @Modifying
    @Query("""
            UPDATE Address a
            SET a.deleted = true
            WHERE a.id = :addressId
            """)
    int softDeleteById(@Param("addressId") Long addressId);

    @Modifying
    @Query("""
            UPDATE Address a
            SET a.deleted = false
            WHERE a.id = :addressId
            """)
    int restoreById(@Param("addressId") Long addressId);

    Optional<Address> findByIdAndUser_Id(Long id, Long userId);

    @Modifying
    @Query("""
            UPDATE Address a
            SET a.deleted = true
            WHERE a.id = :addressId
              AND a.user.id = :userId
            """)
    int softDeleteByUser(@Param("addressId") Long addressId,
                         @Param("userId") Long userId);

    @Modifying
    @Query("""
            UPDATE Address a
            SET a.deleted = false
            WHERE a.id = :addressId
              AND a.user.id = :userId
            """)
    int restoreByUser(@Param("addressId") Long addressId,
                      @Param("userId") Long userId);

    @Query("""
            SELECT a
            FROM Address a
            WHERE a.user.id = :userId
              AND a.deleted = false
            """)
    Page<Address> findAllActiveByUser(@Param("userId") Long userId, Pageable pageable);

    @Modifying
    @Query("""
            UPDATE Address a
            SET a.isDefault = false
            WHERE a.user.id = :userId
              AND a.deleted = false
            """)
    int unsetDefault(@Param("userId") Long userId);

    @Modifying
    @Query("""
            UPDATE Address a
            SET a.isDefault = true
            WHERE a.user.id = :userId
              AND a.id = :addressId
              AND a.deleted = false
            """)
    int setDefault(@Param("userId") Long userId,
                   @Param("addressId") Long addressId);

    @Query("""
            SELECT a
            FROM Address a
            WHERE a.user.id = :userId
              AND a.isDefault = true
              AND a.deleted = false
            """)
    Optional<Address> findDefaultAddressByUserId(@Param("userId") Long userId);
}
