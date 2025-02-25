package hackthevalley.posepik_be.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import hackthevalley.posepik_be.image.entity.UserImgEntity;

@Repository
public interface UserRepository extends JpaRepository<UserImgEntity, Long> {
  boolean existsByNickname(String nickname);
}
