package hoang.shop.identity.repository;

import hoang.shop.identity.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User,Long> {
    Page<User> findByDeletedFalse(Pageable pageable);
    Page<User> findByDeletedTrue(Pageable pageable);
    @Query("""
            SELECT u FROM User u
            WHERE LOWER(u.fullName) LIKE LOWER(CONCAT('%', :keyword, '%'))
               OR LOWER(u.email) LIKE LOWER(CONCAT('%', :keyword, '%'))
            """)
    Page<User> searchByKeyword(@Param("keyword") String keyword,Pageable pageable);
    Optional<User> findByEmail(String email);
    Optional<User> findByPhone(String phone);

    boolean existsByEmail(String email);
    boolean existsByPhone(String phone);
    @Modifying
    @Transactional
    @Query("""
            UPDATE User u SET u.deleted = true WHERE u.id = :id AND u.deleted = false
            """)
    int softDeleteById(@Param("id") Long id);

}
