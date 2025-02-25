package hackthevalley.posepik_be.s3.service;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.Iterator;
import java.util.UUID;

import javax.imageio.ImageIO;
import javax.imageio.plugins.jpeg.JPEGImageWriteParam;
import javax.imageio.stream.ImageOutputStream;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;

@Service
@RequiredArgsConstructor
public class S3Service {
  private static final long MAX_FILE_SIZE = 1024; // 1KB 제한

  private final S3Client s3Client;
  private final S3Presigner s3Presigner;

  @Value("${cloud.aws.s3.bucket}")
  private String bucketName;

  public String uploadFile(MultipartFile file) throws IOException {

    //    System.out.println(">>>>>>>>>>>>>>>>>>>>>>><<<<<<<<<<<<<서비스");
    //    MultipartFile file = compressImageToMultipartFile(f, 100, 100);
    String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();

    PutObjectRequest putObjectRequest =
        PutObjectRequest.builder()
            .bucket(bucketName)
            .key(fileName)
            .contentType(file.getContentType())
            .contentLength(file.getSize())
            .build();

    s3Client.putObject(
        putObjectRequest, RequestBody.fromInputStream(file.getInputStream(), file.getSize()));

    // ✅ `S3Presigner` 사용해서 Pre-signed URL 생성
    GetObjectPresignRequest presignRequest =
        GetObjectPresignRequest.builder()
            .signatureDuration(Duration.ofHours(1))
            .getObjectRequest(GetObjectRequest.builder().bucket(bucketName).key(fileName).build())
            .build();

    return s3Presigner.presignGetObject(presignRequest).url().toString();
  }

  // 파일 삭제
  public void deleteFile(String fileUrl) {
    String fileName = extractFileNameFromUrl(fileUrl);

    DeleteObjectRequest deleteObjectRequest =
        DeleteObjectRequest.builder().bucket(bucketName).key(fileName).build();

    s3Client.deleteObject(deleteObjectRequest);
  }

  // URL에서 파일명 추출하는 메서드
  private String extractFileNameFromUrl(String fileUrl) {
    try {
      URL url = new URL(fileUrl);
      return Paths.get(url.getPath()).getFileName().toString();
    } catch (Exception e) {
      throw new RuntimeException("파일 이름 추출 실패: " + e.getMessage());
    }
  }

  private MultipartFile compressImageToMultipartFile(MultipartFile file, int width, int height)
      throws IOException {
    if (file.getSize() <= MAX_FILE_SIZE) {
      System.out.println("✅ 이미 1KB 이하입니다. 압축 없이 반환합니다.");
      return file;
    }
    BufferedImage originalImage = ImageIO.read(file.getInputStream());
    BufferedImage resizedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

    // 이미지 크기 조정
    Graphics2D g2d = resizedImage.createGraphics();
    g2d.drawImage(originalImage, 0, 0, width, height, null);
    g2d.dispose();

    float quality = 0.9f; // 초기 압축 품질 (90%)
    byte[] compressedBytes;

    do {
      compressedBytes = compressImage(resizedImage, quality);
      quality -= 0.1f; // 품질을 점진적으로 낮춤
    } while (compressedBytes.length > MAX_FILE_SIZE && quality > 0.1f); // 1KB 이하가 될 때까지 반복

    if (compressedBytes.length > MAX_FILE_SIZE) {
      System.out.println("⚠ 압축했지만 1KB 이하로 줄이기 실패. 가장 작은 크기로 반환합니다.");
    } else {
      System.out.println("✅ 최종 압축 완료! 최종 크기: " + compressedBytes.length + " 바이트");
    }

    // 바이트 배열을 MultipartFile로 변환
    return new CustomMultipartFile(file.getOriginalFilename(), compressedBytes, "image/jpeg");
  }

  private byte[] compressImage(BufferedImage image, float quality) throws IOException {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();

    Iterator<javax.imageio.ImageWriter> writers = ImageIO.getImageWritersByFormatName("jpg");
    javax.imageio.ImageWriter writer = writers.next();

    ImageOutputStream ios = ImageIO.createImageOutputStream(baos);
    writer.setOutput(ios);

    JPEGImageWriteParam param = new JPEGImageWriteParam(null);
    param.setCompressionMode(JPEGImageWriteParam.MODE_EXPLICIT);
    param.setCompressionQuality(quality); // 품질 설정 (0.1 ~ 0.9)

    writer.write(null, new javax.imageio.IIOImage(image, null, null), param);

    ios.close();
    writer.dispose();

    return baos.toByteArray();
  }
}
