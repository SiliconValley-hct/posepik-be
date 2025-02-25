package hackthevalley.posepik_be.image.controller;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.web.bind.annotation.*;

import hackthevalley.posepik_be.image.entity.InfluencerImgEntity;
import hackthevalley.posepik_be.image.repository.InfluencerImgRepository;
import hackthevalley.posepik_be.image.repository.UserImgRepository;
import hackthevalley.posepik_be.user.repository.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/influencer-image")
public class InfluencerImgController {
  private final InfluencerImgRepository influencerImgRepository;
  private final UserImgRepository userImgRepository;
  private final UserRepository userRepository;

  @Operation(summary = "메인화면 전체데이터 전송")
  // 전체 데이터를 가져오는 메서드
  @GetMapping("/all")
  public List<Map<String, Object>> getAllInfluencerImageDetails() {
    List<InfluencerImgEntity> influencerImages = influencerImgRepository.findAll();

    // 데이터를 변환하여 반환
    return influencerImages.stream()
        .map(
            influencerImage ->
                Map.<String, Object>of( // 명시적으로 타입 설정
                    "InfluencerImgId", influencerImage.getInfluencerImgId(),
                    "imgInfluencerUrl", influencerImage.getImgInfluencerUrl(),
                    "latitude", influencerImage.getLocation().getLatitude(),
                    "longitude", influencerImage.getLocation().getLongitude(),
                    "country", influencerImage.getLocation().getCountry(),
                    "location", influencerImage.getLocation().getLocation(),
                    "views", influencerImage.getView()))
        .collect(Collectors.toList());
  }

  @Operation(summary = "지역별 인플루언서데이터 전송")
  @GetMapping("/localphoto")
  public List<Map<String, Object>> getLocalInfluencerImage(
      @RequestParam("location") String location) {
    List<InfluencerImgEntity> influencerImages =
        influencerImgRepository.findByLocation_Location(location);

    // 데이터를 변환하여 반환
    return influencerImages.stream()
        .map(
            influencerImage ->
                Map.<String, Object>of(
                    "InfluencerImgId", influencerImage.getInfluencerImgId(),
                    "imgInfluencerUrl", influencerImage.getImgInfluencerUrl(),
                    "comment", influencerImage.getComment(),
                    "country", influencerImage.getLocation().getCountry(),
                    "views", influencerImage.getView()))
        .collect(Collectors.toList());
  }

  @Operation(summary = "지역별 인플루언서데이터 전송")
  @GetMapping("/localphoto/detail")
  public List<Map<String, Object>> getLocalInfluencerImageDetail(
      @RequestParam("InfluencerImgId") Long influencerImgId) {
    List<InfluencerImgEntity> influencerImages =
        influencerImgRepository.findByInfluencerImgId(influencerImgId);

    // 데이터를 변환하여 반환
    return influencerImages.stream()
        .map(
            influencerImage ->
                Map.<String, Object>of(
                    "InfluencerImgId", influencerImage.getInfluencerImgId(),
                    "imgInfluencerUrl", influencerImage.getImgInfluencerUrl(),
                    "comment", influencerImage.getComment(),
                    "photoTip", influencerImage.getComment(),
                    "country", influencerImage.getLocation().getCountry(),
                    "views", influencerImage.getView()))
        .collect(Collectors.toList());
  }

  //  @Operation(summary = "랭킹 업데이트")
  //  @PostMapping("/rankupdate")
  //  public ResponseEntity<String> rankUpdate(@RequestParam("nickname") String nickname) {
  //    // 1. 닉네임 중복 확인
  //    if (userRepository.existsByNickname(nickname)) {
  //      return ResponseEntity.badRequest().body("이미 존재하는 닉네임입니다.");
  //    }
  //
  //    // 2. 새로운 유저 생성
  //    UserEntity newUser = new UserEntity();
  //    newUser.setNickname(nickname);
  //    //    UserEntity savedUser = userRepository.save(newUser);
  //    //
  //    //    // 3. 해당 유저의 이미지 업데이트
  //    //    List<ImageEntity> userImages = imageRepository.findByUserId(savedUser.getUserId());
  //    //    for (ImageEntity image : userImages) {
  //    //      image.setUpdate(true);
  //    //    }
  //    //    imageRepository.saveAll(userImages);
  //
  //    // 4. 응답 반환
  //    return ResponseEntity.ok("유저가 성공적으로 추가되었고 이미지가 업데이트되었습니다.");
  //  }
}
