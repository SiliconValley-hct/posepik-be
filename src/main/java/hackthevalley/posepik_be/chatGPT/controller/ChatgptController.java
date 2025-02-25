package hackthevalley.posepik_be.chatGPT.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import hackthevalley.posepik_be.chatGPT.service.ChatgptCustomService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/v1/chat-gpt")
public class ChatgptController {

  private final ChatgptCustomService chatgptService;

  @PostMapping(value = "/ask", consumes = "multipart/form-data")
  @Operation(summary = "이미지 기반 요청", description = "이미지만 전송하면 고정된 설명과 함께 GPT가 위치 정보를 반환")
  public ResponseEntity<String> askWithImage(@RequestPart("image") MultipartFile imageFile) {
    String response = chatgptService.getChatGptResponse(imageFile);
    return ResponseEntity.ok(response);
  }
}
