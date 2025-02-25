package hackthevalley.posepik_be.user.entity;

import jakarta.persistence.*;

import hackthevalley.posepik_be.image.entity.UserImgEntity;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "user")
public class UserEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "user_id")
  private Long id;

  @OneToOne
  @JoinColumn(name = "image_id", referencedColumnName = "image_id")
  private UserImgEntity image;

  @Column(nullable = false, unique = true)
  private String nickname;
}
