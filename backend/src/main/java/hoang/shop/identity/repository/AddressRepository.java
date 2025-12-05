package hoang.shop.identity.repository;

import hoang.shop.identity.model.Address;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
public interface AddressRepository extends JpaRepository<Address,Long> {
    @Modifying
    @Transactional
    @Query("UPDATE Address a SET deleted = false WHERE id= :id")
    int restoreById(@Param("id") Long id);
    @Modifying
    @Transactional
    @Query("UPDATE Address a SET deleted = true WHERE id= :id")
    int softDeleteById(@Param("id") Long id);
    Page<Address> findAllByIdAndDeletedFalse(Long addressId, Pageable pageable);
    @Modifying
    @Query("""
            UPDATE
                Address a
                SET a.defaultAddress = false
            WHERE
                a.user.id = :userId
                AND a.deleted = false
            """)
    int unsetDefault(@Param("userId") Long userId);
    @Modifying
    @Query("""
            UPDATE Address a
                SET a.defaultAddress = true
            WHERE a.user.id= :userId
                AND a.id = :addressId
                AND a.deleted = false
            """)
    int setDefault(@Param("userId") Long userId,@Param("addressId") Long addressId);
    @Modifying
    @Transactional
    @Query("SELECT a FROM Address a WHERE id = :addressId AND defaultAddress = true")
    Optional<Address> findDefaultAddressById(@Param("addressId") Long addressId);
}
