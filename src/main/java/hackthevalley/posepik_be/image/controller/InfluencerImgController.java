package hackthevalley.posepik_be.image.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import hackthevalley.posepik_be.image.entity.InfluencerImgEntity;
import hackthevalley.posepik_be.image.entity.UserImgEntity;
import hackthevalley.posepik_be.image.repository.InfluencerImgRepository;
import hackthevalley.posepik_be.image.repository.UserImgRepository;
import hackthevalley.posepik_be.s3.service.S3Service;
import hackthevalley.posepik_be.user.entity.UserEntity;
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
  private final S3Service s3Service;

  // ✅ 전역 변수 (테스트 용도)
  private String tempUrl;

  @Operation(summary = "메인화면 전체 데이터 전송")
  @GetMapping("/all")
  public Map<String, Object> getAllInfluencerAndTopImages() {
    // 전체 인플루언서 이미지 데이터 가져오기
    List<InfluencerImgEntity> influencerImages = influencerImgRepository.findAll();

    List<Map<String, Object>> influencerImageDetails =
        influencerImages.stream()
            .map(
                influencerImage ->
                    Map.<String, Object>of(
                        "InfluencerImgId", influencerImage.getInfluencerImgId(),
                        "imgInfluencerUrl", influencerImage.getImgInfluencerUrl(),
                        "latitude", influencerImage.getLocation().getLatitude(),
                        "longitude", influencerImage.getLocation().getLongitude(),
                        "country", influencerImage.getLocation().getCountry(),
                        "location", influencerImage.getLocation().getLocation(),
                        "views", influencerImage.getView()))
            .collect(Collectors.toList());

    // 조회수가 높은 인플루언서 이미지 상위 5개 가져오기
    List<InfluencerImgEntity> topInfluencerImages =
        influencerImgRepository.findTop5ByOrderByViewDesc();

    List<Map<String, Object>> topInfluencerImageDetails =
        topInfluencerImages.stream()
            .map(
                influencerImage ->
                    Map.<String, Object>of(
                        "imgUrl", influencerImage.getImgInfluencerUrl(),
                        "snsAddress", influencerImage.getInfluencer().getSns_id(), // 인플루언서의 SNS 주소
                        "comment", influencerImage.getComment() // 인플루언서 코멘트
                        ))
            .collect(Collectors.toList());

    // 전체 데이터를 하나의 Map으로 통합
    return Map.of(
        "influencerImages", influencerImageDetails,
        "topInfluencerImages", topInfluencerImageDetails);
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

  @Operation(summary = "지역별 인플루언서 데이터 전송")
  @GetMapping("/localphoto/detail")
  public Map<String, Object> getLocalInfluencerImageDetail(
      @RequestParam("InfluencerImgId") Long influencerImgId) {

    // 요청된 InfluencerImgId에 대한 상세 정보 조회
    List<InfluencerImgEntity> influencerImages =
        influencerImgRepository.findByInfluencerImgId(influencerImgId);

    // 조회수가 높은 상위 5개의 이미지 조회 (is_update가 true인 것 중에서)
    List<UserImgEntity> topUserImages = userImgRepository.findTop5ByUpdateTrueOrderByAccuracyDesc();

    // 요청된 인플루언서 이미지 데이터 변환
    List<Map<String, Object>> influencerImageDetails =
        influencerImages.stream()
            .map(
                influencerImage ->
                    Map.<String, Object>of(
                        "InfluencerImgId", influencerImage.getInfluencerImgId(),
                        "imgInfluencerUrl", influencerImage.getImgInfluencerUrl(),
                        "comment", influencerImage.getComment(),
                        "photoTip", influencerImage.getPhotoTip(),
                        "snsId", influencerImage.getInfluencer().getSns_id(),
                        "country", influencerImage.getLocation().getCountry(),
                        "views", influencerImage.getView()))
            .collect(Collectors.toList());

    // 상위 5개 이미지 데이터 변환
    List<Map<String, Object>> topImages =
        topUserImages.stream()
            .map(
                userImage ->
                    Map.<String, Object>of(
                        "imgUrl", userImage.getImgUrl(),
                        "nickName", userImage.getUser().getNickname(), // 유저 닉네임을 SNS ID처럼 활용
                        "views", userImage.getAccuracy()))
            .collect(Collectors.toList());

    // 반환할 데이터 맵 생성
    Map<String, Object> response = new HashMap<>();
    response.put("influencerDetails", influencerImageDetails);
    response.put("topUserImages", topImages);

    return response;
  }

  @Operation(summary = "랭킹 업데이트")
  @PostMapping("/rankupdate")
  public ResponseEntity<String> rankUpdate(@RequestParam("nickname") String nickname) {
    // 1. 닉네임 중복 확인
    if (userRepository.existsByNickname(nickname)) {
      return ResponseEntity.badRequest().body("이미 존재하는 닉네임입니다.");
    }

    // 2. 새로운 유저 생성
    UserEntity newUser = new UserEntity();
    newUser.setNickname(nickname);
    UserEntity savedUser = userRepository.save(newUser);

    // 3. 해당 유저의 이미지 업데이트
    List<UserImgEntity> userImages = userImgRepository.findByUser(savedUser);
    for (UserImgEntity image : userImages) {
      image.setUpdate(true); // 이미지 업데이트 상태 설정
    }
    userImgRepository.saveAll(userImages);

    // 4. 응답 반환
    return ResponseEntity.ok("유저가 성공적으로 추가되었고 이미지가 업데이트되었습니다.");
  }

  /** ✅ 사진 업로드 */
  @Operation(summary = "사진 업로드 및 URL 저장")
  @PostMapping("/take")
  public ResponseEntity<Map<String, Object>> uploadPhoto(@RequestParam("file") MultipartFile file) {
    try {
      // S3에 파일 업로드
      tempUrl = s3Service.uploadFile(file);

      // 응답 반환
      Map<String, Object> response = new HashMap<>();
      response.put("status", 200);
      response.put("message", "사진이 성공적으로 업로드되었습니다.");
      response.put("tempUrl", tempUrl);

      return ResponseEntity.ok(response);
    } catch (Exception e) {
      return ResponseEntity.status(500).body(Map.of("error", "파일 업로드에 실패했습니다."));
    }
  }

  /** ✅ 업로드된 URL을 통한 정확도 반환 */
  @Operation(summary = "정확도 조회")
  @GetMapping("/accuracy")
  public ResponseEntity<Map<String, Object>> getAccuracy() {
    if (tempUrl == null) {
      return ResponseEntity.badRequest().body(Map.of("error", "업로드된 사진이 없습니다."));
    }

    // 임의로 정확도 계산 (AI 분석 대체)
    double accuracy = Math.random() * 100;

    Map<String, Object> response = new HashMap<>();
    response.put("status", 200);
    response.put("accuracy", accuracy);
    response.put("imageUrl", tempUrl);

    return ResponseEntity.ok(response);
  }
}
