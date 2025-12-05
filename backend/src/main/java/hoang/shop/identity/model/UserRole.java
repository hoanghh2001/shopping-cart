package hoang.shop.identity.model;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "user_roles",
        uniqueConstraints = @UniqueConstraint(name = "uk_user_role",columnNames = {"user_id","role_id"}))
@Getter @Setter
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class UserRole {
    @lombok.Builder
    private UserRole(User user,Role role,Long assignedBy){
        this.user = user;
        this.role = role;
        this.assignedBy=assignedBy;
        this.id = new UserRoleId(user.getId(),role.getId());
    }




    @EmbeddedId
    @EqualsAndHashCode.Include
    private UserRoleId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("userId")
    @JoinColumn(name = "user_id",
                foreignKey = @ForeignKey(name = "fk_user_roles_user"))
    @JsonIgnore
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("roleId")
    @JoinColumn(name = "role_id",
                foreignKey = @ForeignKey(name = "fk_user_roles_role"))
    @JsonIgnore
    private Role role;

    @Column(name = "assigned_by")
    private Long assignedBy;
    @Column(name = "assigned_at")
    private Instant assignedAt;
    @Column(name = "removed_by")
    private Long removedBy;
    @Column(name = "removed_at")
    private Instant removedAt;
    @Column(nullable = false)
    private boolean deleted = false;
}
