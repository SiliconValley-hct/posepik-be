package hackthevalley.posepik_be.image.service;

import java.util.Map;

import org.springframework.stereotype.Service;

import hackthevalley.posepik_be.image.entity.InfluencerImgEntity;
import hackthevalley.posepik_be.image.repository.InfluencerImgRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class InfluencerImgService {

  private final InfluencerImgRepository influencerImgRepository;

  public Map<String, Object> getAllInfluencerImageDetails(Long mappingId) {
    InfluencerImgEntity influencerImage =
        influencerImgRepository
            .findById(mappingId)
            .orElseThrow(() -> new RuntimeException("해당 매핑 ID로 데이터를 찾을 수 없습니다."));

    // 엔티티 객체로부터 직접 데이터를 가져오기
    return Map.of(
        "InfluencerImgId", influencerImage.getInfluencerImgId(),
        "imgInfluencerUrl", influencerImage.getImgInfluencerUrl(),
        "photoTip", influencerImage.getPhotoTip(),
        "latitude", influencerImage.getLocation().getLatitude(),
        "longitude", influencerImage.getLocation().getLongitude(),
        "country", influencerImage.getLocation().getCountry(),
        "location", influencerImage.getLocation().getLocation());
  }
}
