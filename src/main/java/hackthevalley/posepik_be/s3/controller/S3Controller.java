package hackthevalley.posepik_be.s3.controller;

import java.io.IOException;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import hackthevalley.posepik_be.s3.service.S3Service;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("api/s3")
@RequiredArgsConstructor
public class S3Controller {

  private final S3Service s3Service;

  @Operation(
      summary = "파일 업로드",
      description = "S3에 파일을 업로드하고 URL을 반환합니다.",
      security =
          @io.swagger.v3.oas.annotations.security.SecurityRequirement(
              name = "bearerAuth") // JWT 인증 적용,
      )
  @PostMapping(
      value = "/upload",
      consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file) {
    try {
      String fileUrl = s3Service.uploadFile(file);
      return ResponseEntity.ok(fileUrl);
    } catch (IOException e) {
      return ResponseEntity.badRequest().body("File upload failed: " + e.getMessage());
    }
  }

  // 파일 삭제 API
  @Operation(
      summary = "파일 삭제",
      description = "S3에 파일을 업로드있는 URL을 통해 삭제",
      security =
          @io.swagger.v3.oas.annotations.security.SecurityRequirement(
              name = "bearerAuth") // JWT 인증 적용,
      )
  @DeleteMapping("/delete")
  public ResponseEntity<String> deleteFile(@RequestParam("fileUrl") String fileUrl) {
    try {
      s3Service.deleteFile(fileUrl);
      return ResponseEntity.ok("파일 삭제 성공");
    } catch (Exception e) {
      return ResponseEntity.badRequest().body("파일 삭제 실패: " + e.getMessage());
    }
  }
}
