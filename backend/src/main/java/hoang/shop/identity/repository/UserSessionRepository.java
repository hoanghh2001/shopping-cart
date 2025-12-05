package hoang.shop.identity.repository;

import hoang.shop.identity.model.UserSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface UserSessionRepository extends JpaRepository<UserSession, Long> {

    List<UserSession> findByUser_IdAndRevokedAtIsNullAndExpiresAtAfter(Long userId, Instant now);

    Optional<UserSession> findByRefreshTokenAndRevokedAtIsNullAndExpiresAtAfter(String refreshToken, Instant now);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
            UPDATE UserSession us
            SET us.revokedAt = :revokedAt
            WHERE us.id = :sessionId
              AND us.user.id = :userId
              AND us.revokedAt IS NULL
            """)
    int revokeByIdAndUserId(Long sessionId, Long userId, Instant revokedAt);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
            UPDATE UserSession us
            SET us.revokedAt = :revokedAt
            WHERE us.user.id = :userId
              AND us.revokedAt IS NULL
            """)
    void revokeAllByUserId(Long userId, Instant revokedAt);


    @Modifying
    @Query("""
    UPDATE UserSession s
    SET s.revokedAt = :now
    WHERE s.user.id = :userId AND s.revokedAt IS NULL AND s.expiresAt > :now
    """)
    void revokeAllActiveSessionsByUserId(@Param("userId") Long userId, @Param("now")Instant now);
}
