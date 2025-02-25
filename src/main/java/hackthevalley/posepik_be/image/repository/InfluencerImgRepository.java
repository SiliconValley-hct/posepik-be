package hackthevalley.posepik_be.image.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import hackthevalley.posepik_be.image.entity.InfluencerImgEntity;

@Repository
public interface InfluencerImgRepository extends JpaRepository<InfluencerImgEntity, Long> {
  List<InfluencerImgEntity> findByLocation_Location(String location);

  List<InfluencerImgEntity> findByInfluencerImgId(Long influencerImgId);

  // 조회수가 높은 상위 5개 이미지 조회
  List<InfluencerImgEntity> findTop5ByOrderByViewDesc();

  @Query("SELECT i.imgInfluencerUrl FROM InfluencerImgEntity i WHERE i.influencerImgId = ?1")
  String findImgInfluencerUrlByInfluencerImgId(Long influencerImgId);
}
