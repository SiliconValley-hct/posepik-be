package hackthevalley.posepik_be.image.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import hackthevalley.posepik_be.image.entity.InfluencerImgEntity;

@Repository
public interface InfluencerImgRepository extends JpaRepository<InfluencerImgEntity, Long> {
  List<InfluencerImgEntity> findByLocation_Location(String location);

  List<InfluencerImgEntity> findByInfluencerImgId(Long influencerImgId);
}
