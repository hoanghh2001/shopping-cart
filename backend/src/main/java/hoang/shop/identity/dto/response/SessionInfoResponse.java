package hoang.shop.identity.dto.response;

import java.time.Instant;

public record SessionInfoResponse(
        Long id,
        String device,
        String ipAddress,
        Instant createdAt,
        Instant expiresAt,
        boolean current
) {
}
