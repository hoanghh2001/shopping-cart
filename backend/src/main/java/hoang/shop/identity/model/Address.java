package hoang.shop.identity.model;
import jakarta.persistence.*;
import lombok.*;
import hoang.shop.common.baseEntity.BaseEntity;


@Entity
@Table(name = "addresses",
        indexes = @Index(name = "ix_addresses_user_id",columnList = "user_id"))
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class Address extends BaseEntity {
    @ManyToOne
    @JoinColumn(name = "user_id",nullable = false,
                foreignKey = @ForeignKey(name = "fk_addresses_user_id_users"))
    private User user;

    private String fullName;

    @Column(nullable = false)
    private String phone;

    @Column(name = "postal_code",nullable = false)
    private String postalCode;

    @Column(name = "address_line1",nullable = false)
    private String addressLine1;

    @Column(name = "address_line2",nullable = false)
    private String addressLine2;

    @Column(nullable = false)
    private String city;

    @Column(nullable = false)
    private String state;

    @Column(nullable = false)
    private String country = "Japan";
    @Column(name = "is_default",nullable = false)
    private boolean defaultAddress = true;
    @Column(nullable = false)
    private boolean deleted = false;

}
