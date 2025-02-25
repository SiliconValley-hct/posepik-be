package hackthevalley.posepik_be.image.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import hackthevalley.posepik_be.image.entity.UserImgEntity;
import hackthevalley.posepik_be.user.entity.UserEntity;

@Repository
public interface UserImgRepository extends JpaRepository<UserImgEntity, Long> {
  List<UserImgEntity> findByUserId(Long userId);

  // 해당 유저의 이미지 가져오기
  List<UserImgEntity> findByUser(UserEntity user);

  // is_update가 true이고 view가 높은 순으로 5개 가져오기
  @Query("SELECT u FROM UserImgEntity u WHERE u.update = true ORDER BY u.accuracy DESC")
  List<UserImgEntity> findTop5ByUpdateTrueOrderByAccuracyDesc();
}
