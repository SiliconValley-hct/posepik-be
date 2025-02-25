package hackthevalley.posepik_be.image.entity;

import jakarta.persistence.*;

import hackthevalley.posepik_be.location.entity.LocationEntity;
import hackthevalley.posepik_be.user.entity.UserEntity;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "user_img")
public class UserImgEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "image_id")
  private Long id;

  @ManyToOne
  @JoinColumn(name = "location_id", nullable = false)
  private LocationEntity location;

  private String imgUrl;
  private Double accuracy;

  @OneToOne(mappedBy = "image")
  private UserEntity user;

  @Column(name = "is_update")
  private boolean update;
}
