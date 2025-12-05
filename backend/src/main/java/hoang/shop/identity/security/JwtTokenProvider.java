package hoang.shop.identity.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

@Component
public class JwtTokenProvider {

    // Secret key – đủ dài cho HS256
    private final String jwtSecret = "hoang-shopping-cart-super-secret-key-123";

    // Thời gian sống access token (ms) – ví dụ 1 ngày
    private final long accessTokenTtlMs = 24 * 60 * 60 * 1000L;

    // Thời gian sống refresh token (ms) – ví dụ 7 ngày
    private final long refreshTokenTtlMs = 7L * 24 * 60 * 60 * 1000L;

    // Thời gian sống token reset password (ms) – ví dụ 30 phút
    private final long passwordResetTtlMs = 30 * 60 * 1000L;

    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
    }

    // =========== ACCESS TOKEN ===========

    public String generateAccessToken(UserPrincipal principal) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + accessTokenTtlMs);

        return Jwts.builder()
                .setSubject(principal.getEmail())   // subject = email
                .claim("id", principal.getId())     // userId
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    // =========== REFRESH TOKEN ===========

    public String generateRefreshToken(Long userId) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + refreshTokenTtlMs);

        return Jwts.builder()
                .setSubject(String.valueOf(userId)) // subject = userId
                .claim("type", "refresh")
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    // =========== COMMON UTIL ===========

    public String getEmailFromToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claims.getSubject();
    }

    public Long getUserIdFromToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();

        Object id = claims.get("id");
        if (id != null) {
            return Long.parseLong(id.toString());
        }

        String subject = claims.getSubject();
        try {
            return Long.parseLong(subject);
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    public boolean validateToken(String authToken) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(authToken);
            return true;
        } catch (JwtException | IllegalArgumentException ex) {
            return false;
        }
    }

    // =========== TTL GETTERS ===========

    public long getAccessTokenTtlMs() {
        return accessTokenTtlMs;
    }

    public long getRefreshTokenTtlMs() {
        return refreshTokenTtlMs;
    }

    public long getPasswordResetTtlMs() {
        return passwordResetTtlMs;
    }
}
