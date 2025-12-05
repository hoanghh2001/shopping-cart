package hoang.shop.identity.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.*;
import hoang.shop.cart.model.Cart;
import hoang.shop.common.baseEntity.BaseEntity;
import hoang.shop.common.enums.Gender;
import hoang.shop.order.model.Order;
import org.hibernate.annotations.*;

import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "users",
    indexes = {
        @Index(name = "ix_users_email", columnList = "email",unique = true),
        @Index(name = "ix_users_status", columnList = "deleted")
    })
@SQLDelete(sql = "UPDATE users SET deleted = true WHERE id = ?")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class User extends BaseEntity {
    //constrain
    @OneToMany(mappedBy = "user",cascade = CascadeType.ALL,orphanRemoval = true,fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Order> orders = new ArrayList<>();
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL,orphanRemoval = true,fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Address> address = new ArrayList<>();

    @OneToMany(mappedBy = "user",cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonIgnore
    private Set<UserRole> userRoles = new HashSet<>();
    @OneToMany(mappedBy = "user",cascade = CascadeType.ALL,orphanRemoval = true,fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Cart> carts = new ArrayList<>();

    //field
    @Column(name = "first_name",nullable = false,length = 100)
    private String firstName;

    @Column(name = "last_name",nullable = false,length = 100)
    private String lastName;


    @Column(name = "full_name",insertable = false,updatable = false,columnDefinition = "GENERATED ALWAYS AS (CONCAT(first_name, ' ', last_name)) STORED")
    private String fullName;

    @Enumerated(EnumType.STRING)
    @Column( length = 16)
    private Gender gender;

    private LocalDate birthday;

    @Column(length = 255,nullable = false)
    private String email;

    @ToString.Exclude
    @JsonIgnore
    @Column(nullable = false)
    private String password;

    @Column(length=16)
    private String phone;

    @Column(name = "avatar_url",length = 255)
    @JsonIgnore
    private String avatarUrl;

    @Column(name = "email_verified")
    private boolean emailVerified;

    @Column(name = "last_login")
    private Instant lastLogin;
    @Column(nullable = false)
    private boolean deleted = false;
}