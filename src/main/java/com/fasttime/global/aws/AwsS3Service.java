package com.fasttime.global.aws;

import java.net.URL;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

@Service
@RequiredArgsConstructor
@Slf4j
public class AwsS3Service {
  private final S3Presigner s3Presigner;
  private final String bucketPrefix;

  @Autowired
  public AwsS3Service(
      S3Presigner s3Presigner,
      @Value("${aws.s3.bucket}") String bucket,
      @Value("${aws.s3.prefix}") String prefix) {
    this.s3Presigner = s3Presigner;
    this.bucketPrefix = bucket + "/" + prefix;
  }

  private String buildPreSignedUrl(String fileName) {
    return s3Presigner
        .presignGetObject(
            r ->
                r.signatureDuration(Duration.ofMinutes(10))
                    .getObjectRequest(gor -> gor.bucket(bucketPrefix).key(fileName)))
        .url()
        .toString();
  }

  public String getPreSignedUrl(String fileName) {
    return buildPreSignedUrl(fileName);
  }

  public List<String> generateMultiplePresignedUrls(List<String> objectKeys) {
    return objectKeys.stream().map(this::buildPreSignedUrl).toList();
  }
}