package hackthevalley.posepik_be.user.entity;

import jakarta.persistence.*;

import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "user")
public class UserEntity {

  @Id
  @GeneratedValue
  @Column(name = "user_id")
  private Long id;

  @Column(nullable = false)
  private String nickname;
}
