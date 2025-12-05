package hoang.shop.identity.repository;

import hoang.shop.identity.model.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
@Repository
public interface RoleRepository extends JpaRepository<Role,Long> {

    Optional<Role> findByName(String name);
    boolean existsByNameIgnoreCase(String name);
    List<Role> findByNameIn(Collection<String> names);
    @Query("SELECT r FROM Role r WHERE LOWER(r.name) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<Role> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);
    @Modifying
    @Transactional
    @Query("UPDATE Role r SET r.deleted = true WHERE r.id = :id")
    int softDeleteById(@Param("id") Long id);
    @Modifying
    @Transactional
    @Query("UPDATE Role r SET r.deleted = false WHERE r.id = :id")
    int restoreById(@Param("id") Long id);
    Page<Role> findAllByDeletedFalse(Pageable pageable);
    Page<Role> findAllByDeletedTrue(Pageable pageable);

}
