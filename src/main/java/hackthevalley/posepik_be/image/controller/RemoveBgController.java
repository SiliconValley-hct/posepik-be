package hackthevalley.posepik_be.image.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import hackthevalley.posepik_be.image.service.RemoveBgService;
import hackthevalley.posepik_be.opencv.ImageSimilarityService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/removebg")
@RequiredArgsConstructor
public class RemoveBgController {

  private final RemoveBgService removeBgService;

  @PostMapping(value = "/remove", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<byte[]> removeBackground(@RequestPart("image") MultipartFile imageFile) {
    try {
      byte[] resultImage = removeBgService.removeBackground(imageFile);

      HttpHeaders headers = new HttpHeaders();
      headers.add("Content-Type", "image/png");

      return new ResponseEntity<>(resultImage, headers, HttpStatus.OK);
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
    }
  }
}
