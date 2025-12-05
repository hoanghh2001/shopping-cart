package hoang.shop.identity.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RoleCreateRequest(
        @NotBlank(message = "{role.name.notBlank}")
        @Size(message = "{role.name.size}")
        String name,

        @NotBlank(message = "{role.description.notBlank}")
        @Size(message = "{role.description.size}")
        String description

) {
}
