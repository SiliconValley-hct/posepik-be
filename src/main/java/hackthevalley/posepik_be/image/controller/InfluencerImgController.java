package hackthevalley.posepik_be.image.controller;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.web.bind.annotation.*;

import hackthevalley.posepik_be.image.entity.InfluencerImgEntity;
import hackthevalley.posepik_be.image.repository.InfluencerImgRepository;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/influencer-image")
public class InfluencerImgController {
  private final InfluencerImgRepository influencerImgRepository;

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
                    "mappingId", influencerImage.getId(),
                    "imgInfluencerUrl", influencerImage.getImgInfluencerUrl(),
                    "photoTip", influencerImage.getPhotoTip(),
                    "latitude", influencerImage.getLocation().getLatitude(),
                    "longitude", influencerImage.getLocation().getLongitude(),
                    "country", influencerImage.getLocation().getCountry(),
                    "location", influencerImage.getLocation().getLocation()))
        .collect(Collectors.toList());
  }
}
