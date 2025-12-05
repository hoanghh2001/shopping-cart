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

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne
    @JoinColumn(name = "user_id",nullable = false,
                foreignKey = @ForeignKey(name = "fk_addresses_user_id_users"))
    private User user;

    @Column(name = "name",nullable = false)
    private String name;

    @Column(nullable = false)
    private String phone;

    @Column(name = "postal_code",nullable = false)
    private String postalCode;

    @Column(nullable = false)
    private String prefecture;

    @Column(nullable = false)
    private String municipality;

    private Integer chome;

    private Integer ban;

    private Integer go;
    @Column(name = "street_number",nullable = false)
    private String streetNumber;

    @Column(name = "building")
    private String building;
    @Column(name = "room-number")
    private String roomNumber;
    @Column(name = "full_address")
    private String fullAddress;

    @Column(nullable = false)
    private String country = "Japan";

    @Column(name = "is_default",nullable = false)
    private boolean isDefault = false;

    @Column(nullable = false)
    private boolean deleted = false;

}
