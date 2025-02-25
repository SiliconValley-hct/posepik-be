package hackthevalley.posepik_be.image.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import hackthevalley.posepik_be.image.entity.InfluencerImgEntity;

@Repository
public interface InfluencerImgRepository extends JpaRepository<InfluencerImgEntity, Long> {
  // JpaRepository의 findAll() 메서드 활용
}
