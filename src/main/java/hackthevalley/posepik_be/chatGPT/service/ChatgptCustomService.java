package hackthevalley.posepik_be.chatGPT.service;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatgptCustomService {
  @Value("${CHATGPT_API}")
  private String apiKey;

  private final String API_URL = "https://api.openai.com/v1/chat/completions";
  private final RestTemplate restTemplate = new RestTemplate();
  private final ObjectMapper objectMapper = new ObjectMapper(); // JSON 파싱용

  // 이미지를 기반으로 요청 (설명은 고정)
  public String getChatGptResponse(MultipartFile imageFile) {
    try {
      // 이미지를 Base64로 인코딩
      String base64Image = Base64.encodeBase64String(imageFile.getBytes());

      // 고정된 질문 내용 생성
      String question =
          "나는 이 사진을 최대한 똑같이 찍고 싶은 여행자야. "
              + "에펠탑 어느 쪽에서 찍어야 되는지 알려줄 수 있어? "
              + "자세한 위치, 그리고 동서남북 방향으로 알려주면 좋을 것 같아. "
              + "-습니다체로 끝나도록 해주고 공백 포함 125자를 넘어서지 않게 알려줘. "
              + "[이미지 데이터: "
              + base64Image
              + "]";

      // GPT 요청
      return sendRequestToGPT(question);
    } catch (Exception e) {
      // Base64 인코딩 실패 시 로그 출력
      log.error("이미지 Base64 인코딩 중 오류 발생: ", e);
      return "이미지를 처리하는 중 오류가 발생했습니다.";
    }
  }

  // GPT 요청 전송 로직
  private String sendRequestToGPT(String question) {
    Map<String, Object> requestBody = new HashMap<>();
    requestBody.put("model", "gpt-3.5-turbo");
    requestBody.put(
        "messages",
        Arrays.asList(
            Map.of("role", "system", "content", "You are a helpful assistant."),
            Map.of("role", "user", "content", question)));
    requestBody.put("max_tokens", 500);
    requestBody.put("temperature", 0.7);

    // HTTP 요청 헤더 설정
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.setBearerAuth(apiKey);

    HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

    // API 호출
    ResponseEntity<String> response =
        restTemplate.exchange(API_URL, HttpMethod.POST, request, String.class);

    return extractContent(response.getBody()); // JSON에서 "content" 값만 추출
  }

  // JSON에서 "content" 값만 추출하는 메서드
  private String extractContent(String jsonResponse) {
    try {
      JsonNode rootNode = objectMapper.readTree(jsonResponse);
      return rootNode.path("choices").get(0).path("message").path("content").asText();
    } catch (Exception e) {
      return "응답을 처리하는 중 오류가 발생했습니다.";
    }
  }
}
