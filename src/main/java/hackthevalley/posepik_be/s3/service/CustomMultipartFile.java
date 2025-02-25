package hackthevalley.posepik_be.s3.service;

import java.io.*;

import org.springframework.web.multipart.MultipartFile;

public class CustomMultipartFile implements MultipartFile {

  private final String fileName;
  private final byte[] content;
  private final String contentType;

  public CustomMultipartFile(String fileName, byte[] content, String contentType) {
    this.fileName = fileName;
    this.content = content;
    this.contentType = contentType;
  }

  @Override
  public String getName() {
    return fileName;
  }

  @Override
  public String getOriginalFilename() {
    return fileName;
  }

  @Override
  public String getContentType() {
    return contentType;
  }

  @Override
  public boolean isEmpty() {
    return content.length == 0;
  }

  @Override
  public long getSize() {
    return content.length;
  }

  @Override
  public byte[] getBytes() throws IOException {
    return content;
  }

  @Override
  public InputStream getInputStream() throws IOException {
    return new ByteArrayInputStream(content);
  }

  @Override
  public void transferTo(File dest) throws IOException {
    try (FileOutputStream fos = new FileOutputStream(dest)) {
      fos.write(content);
    }
  }
}
