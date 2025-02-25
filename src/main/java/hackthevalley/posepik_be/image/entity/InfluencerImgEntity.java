package hackthevalley.posepik_be.image.entity;

import hackthevalley.posepik_be.location.entity.LocationEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "influencer_img")
public class InfluencerImgEntity {
    @Id
    @GeneratedValue
    @Column(name = "influencerImg_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "location_id", nullable = false)
    private LocationEntity location;

    private Long view;
    private String outlineImgUrl;
    private String imgInfluencerUrl;
    private String photoTip;
}
