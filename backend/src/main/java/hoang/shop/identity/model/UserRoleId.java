package hoang.shop.identity.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;

@Embeddable
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class UserRoleId implements Serializable {
    @EqualsAndHashCode.Include
    @Column(name = "user_id",nullable = false)
    private Long userId;
    @EqualsAndHashCode.Include
    @Column(name = "role_id",nullable = false)
    private Long roleId;




}

