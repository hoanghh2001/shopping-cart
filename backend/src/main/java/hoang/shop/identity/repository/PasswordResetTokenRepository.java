package hoang.shop.identity.repository;

import hoang.shop.identity.model.PasswordResetToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.Optional;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {

    Optional<PasswordResetToken> findByTokenAndUsedAtIsNull(String token);

    @Modifying
    @Query("""
        UPDATE PasswordResetToken t
        SET t.used = true,
            t.usedAt = :now
        WHERE t.user.id = :userId
          AND t.used = false
          AND t.expiresAt > :now
        """)
    void invalidateAllForUser(
            @Param("userId") Long userId,
            @Param("now") Instant now
    );

    @Modifying
    @Query("DELETE FROM PasswordResetToken t WHERE t.user.id = :userId")
    void deleteByUserId(@Param("userId") Long userId);
}
