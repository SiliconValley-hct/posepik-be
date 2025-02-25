package hackthevalley.posepik_be.image.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import hackthevalley.posepik_be.image.entity.UserImgEntity;

@Repository
public interface UserImgRepository extends JpaRepository<UserImgEntity, Long> {
  List<UserImgEntity> findByUserId(Long userId);
}
