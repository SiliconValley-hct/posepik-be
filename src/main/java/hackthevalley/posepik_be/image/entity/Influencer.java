package hackthevalley.posepik_be.image.entity;

import jakarta.persistence.*;

import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Influencer {
  @Id
  @GeneratedValue
  @Column(name = "influencer_id")
  private Long id;

  @Column(nullable = false)
  private String name;

  @Column(nullable = false)
  private String sns_id;
}
