package hackthevalley.posepik_be.image.entity;

import jakarta.persistence.*;

import hackthevalley.posepik_be.location.entity.LocationEntity;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "influencer_img")
public class InfluencerImgEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long influencerImgId;

  @ManyToOne
  @JoinColumn(name = "location_id", nullable = false)
  private LocationEntity location;

  @ManyToOne
  @JoinColumn(name = "influencer_id", nullable = false)
  private Influencer influencer;

  private Long view;
  private String outlineImgUrl;
  private String imgInfluencerUrl;
  private String photoTip;
  private String comment;
}
