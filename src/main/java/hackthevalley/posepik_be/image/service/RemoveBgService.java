package hackthevalley.posepik_be.image.service;

import static org.bytedeco.opencv.global.opencv_imgcodecs.*;

import java.io.IOException;

import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.entity.mime.MultipartEntityBuilder;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RemoveBgService {

  @Value("${removebg.api.key}")
  private String apiKey;

  private final String REMOVE_BG_API_URL = "https://api.remove.bg/v1.0/removebg";

  // remove.bg API 호출
  public byte[] removeBackground(MultipartFile imageFile) throws IOException {
    try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
      HttpPost httpPost = new HttpPost(REMOVE_BG_API_URL);
      httpPost.addHeader("X-Api-Key", apiKey);

      // 이미지 파일 전송
      HttpEntity entity =
          MultipartEntityBuilder.create()
              .addBinaryBody(
                  "image_file",
                  imageFile.getInputStream(),
                  org.apache.hc.core5.http.ContentType.DEFAULT_BINARY,
                  imageFile.getOriginalFilename())
              .addTextBody("size", "auto")
              .build();

      httpPost.setEntity(entity);

      try (CloseableHttpResponse response = httpClient.execute(httpPost)) {
        int statusCode = response.getCode();
        if (statusCode == 200) {
          return EntityUtils.toByteArray(response.getEntity());
        } else {
          throw new RuntimeException("Failed to remove background");
        }
      }
    }
  }
}
