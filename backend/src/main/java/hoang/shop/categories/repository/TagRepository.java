package hoang.shop.categories.repository;

import hoang.shop.categories.model.Tag;
import hoang.shop.common.enums.TagStatus;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface TagRepository extends JpaRepository<Tag,Long> {
    @Modifying
    @Query("""
            UPDATE Tag t
            SET t.status = :status
            WHERE t.id IN :ids
            """)
    int updateStatusByIdIn(@Param("ids") List<Long> ids,
                           @Param("status") TagStatus status);
    int countByIdInAndStatus(List<Long> ids, TagStatus status);
    boolean existsBySlug(String slug);
    boolean existsByName(String name);
    boolean existsBySlugAndIdNot(String slug,Long id);
    boolean existsByNameAndIdNot(String name,Long id);

    @Query("""
            SELECT t
            FROM Tag t
            WHERE  (:keyword IS NULL
                    OR LOWER(t.name) LIKE LOWER(CONCAT('%', :keyword, '%'))
                    OR LOWER(t.slug) LIKE LOWER(CONCAT('%', :keyword, '%')))
                    AND :status IS NULL OR t.status = :status
            """)
    Slice<Tag> findByKeywordAndStatus(@Param("keyword") String keyword ,
                                      @Param("status") TagStatus status,
                                      Pageable pageable);
    Optional<Tag> findBySlug(String slug);
    Optional<Tag> findByIdAndStatus(Long tagId, TagStatus tagStatus);
    Optional<Tag> findBySlugAndStatus(String slug , TagStatus status);

    List<Tag> findAllByStatus(TagStatus status);
}
