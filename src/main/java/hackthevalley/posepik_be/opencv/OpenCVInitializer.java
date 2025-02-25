package hackthevalley.posepik_be.opencv;

import jakarta.annotation.PostConstruct;

import org.bytedeco.javacpp.Loader;
import org.bytedeco.opencv.global.opencv_core;
import org.opencv.core.Core;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class OpenCVInitializer {
  ImageSimilarityService imageSimilarityService = new ImageSimilarityService();

  @PostConstruct
  public void loadOpenCV() {
    try {
      // Bytedeco 방식으로 OpenCV 네이티브 라이브러리 로드
      Loader.load(opencv_core.class);
      System.out.println("✅ OpenCV Loaded: " + Core.VERSION);
    } catch (Exception e) {
      System.err.println("❌ OpenCV 로드 실패: " + e.getMessage());
    }
    // 예시
    //    imageSimilarityService.calculateSimilarityFromURL(
    //
    // "https://posepik-bucket.s3.us-west-1.amazonaws.com/937c0ec6-ef9c-425c-9aab-a20755df2000_%E1%84%91%E1%85%A1%E1%84%85%E1%85%B5%E1%84%8C%E1%85%A6%E1%84%82%E1%85%B5.jpeg?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Date=20250225T054240Z&X-Amz-SignedHeaders=host&X-Amz-Expires=3600&X-Amz-Credential=AKIAQ3EGTEVGX5MP44X4%2F20250225%2Fus-west-1%2Fs3%2Faws4_request&X-Amz-Signature=f581846db691a02061d55365c388b6b12e3af1e0207b79d3dde790abbad09af4",
    //
    // "https://posepik-bucket.s3.us-west-1.amazonaws.com/937c0ec6-ef9c-425c-9aab-a20755df2000_%E1%84%91%E1%85%A1%E1%84%85%E1%85%B5%E1%84%8C%E1%85%A6%E1%84%82%E1%85%B5.jpeg?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Date=20250225T054240Z&X-Amz-SignedHeaders=host&X-Amz-Expires=3600&X-Amz-Credential=AKIAQ3EGTEVGX5MP44X4%2F20250225%2Fus-west-1%2Fs3%2Faws4_request&X-Amz-Signature=f581846db691a02061d55365c388b6b12e3af1e0207b79d3dde790abbad09af4");
    imageSimilarityService.extractOutlineUrl(
        "https://posepik-bucket.s3.us-west-1.amazonaws.com/745c1b3c-a644-4392-a450-954f4408617a_%E1%84%91%E1%85%A1%E1%84%85%E1%85%B5%E1%84%8C%E1%85%A6%E1%84%82%E1%85%B5.jpeg?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Date=20250225T064551Z&X-Amz-SignedHeaders=host&X-Amz-Expires=3600&X-Amz-Credential=AKIAQ3EGTEVGX5MP44X4%2F20250225%2Fus-west-1%2Fs3%2Faws4_request&X-Amz-Signature=ecaf40e0fe14af27a7bcb82739c35b868b5b2e1bb1afae4b1a816f48b0f7a459",
        "https://posepik-bucket.s3.us-west-1.amazonaws.com/fecbbce2-035a-4f33-b3b4-b97b2c91371e_%E1%84%91%E1%85%A1%E1%84%85%E1%85%B5%E1%84%8C%E1%85%A6%E1%84%82%E1%85%B5-removebg-preview.png?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Date=20250225T064614Z&X-Amz-SignedHeaders=host&X-Amz-Expires=3600&X-Amz-Credential=AKIAQ3EGTEVGX5MP44X4%2F20250225%2Fus-west-1%2Fs3%2Faws4_request&X-Amz-Signature=d80aec74484a345c24b9ff8a658818b8e75e51f19b18497ab22177eaf045c735");
  }
}
