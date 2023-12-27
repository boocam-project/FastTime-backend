package com.fasttime.global.aws;

import static software.amazon.awssdk.services.s3.model.DeleteObjectRequest.*;

import jakarta.annotation.PreDestroy;
import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@Service
@RequiredArgsConstructor
@Slf4j
public class AwsS3Service {
  private final S3Client s3Client;
  private final ExecutorService executorService = Executors.newFixedThreadPool(10);

  @Value("${aws.s3.bucket}")
  private String bucket;

  @Value("${aws.s3.prefix}")
  private String prefix;

  public CompletableFuture<Void> upload(MultipartFile multipartFile) throws IOException {
    return CompletableFuture.runAsync(
        () -> {
          try (InputStream fileStream = multipartFile.getInputStream()) {
            String fileName = generateFileName(multipartFile);

            PutObjectRequest putObjectRequest =
                PutObjectRequest.builder().bucket(bucket).key(fileName).build();

            s3Client.putObject(
                putObjectRequest, RequestBody.fromInputStream(fileStream, multipartFile.getSize()));
          } catch (IOException e) {
            log.error("Error uploading file: {}", e.getMessage());
            throw new RuntimeException("Error uploading file", e);
          }
        },
        executorService);
  }

  @PreDestroy
  public void destroy() {
    if (!executorService.isShutdown()) {
      executorService.shutdown();
    }
  }

  private String generateFileName(MultipartFile multipartFile) {
    String originalFilename = multipartFile.getOriginalFilename();
    String fileExtension =
        Optional.ofNullable(originalFilename)
            .filter(f -> f.contains("."))
            .map(f -> f.substring(originalFilename.lastIndexOf(".") + 1))
            .orElse("");
    return prefix
        + UUID.randomUUID()
        + "_"
        + originalFilename
        + (fileExtension.isEmpty() ? "" : "." + fileExtension);
  }
}