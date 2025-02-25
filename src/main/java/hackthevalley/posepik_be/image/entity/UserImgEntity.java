package hackthevalley.posepik_be.image.entity;

import hackthevalley.posepik_be.location.entity.LocationEntity;
import hackthevalley.posepik_be.user.entity.UserEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;


@Entity
@Getter
@Setter
@Table(name = "userImg")
public class UserImgEntity {

    @Id
    @GeneratedValue
    @Column(name = "image_id")
    private Long id;


    @ManyToOne
    @JoinColumn(name = "location_id", nullable = false)
    private LocationEntity location;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    private String imgUrl;
    private Double accuracy;

}
