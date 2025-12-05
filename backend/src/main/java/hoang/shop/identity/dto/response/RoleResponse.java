package hoang.shop.identity.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import org.hibernate.sql.Update;

import java.awt.*;
import java.time.Instant;
@JsonInclude(JsonInclude.Include.NON_NULL)
public record RoleResponse (
        Long id,
        String name,
        String description,
        Instant createdAt,
        Long createdBy,
        Instant updatedAt,
        Long updatedBy,
        Instant deletedAt,
        Long deletedBy,
        boolean deleted
){
}
