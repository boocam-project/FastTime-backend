package com.fasttime.global.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

@Configuration
public class AwsS3Config {

  private String accessKey;
  private String secretKey;
  private String region;

  public AwsS3Config(
      @Value("${aws.s3.access-key}") String accessKey,
      @Value("${aws.s3.secret-key}") String secretKey,
      @Value("${aws.s3.region}") String region) {
    this.accessKey = accessKey;
    this.secretKey = secretKey;
    this.region = region;
  }

  @Bean
  public AwsCredentials basicAWSCredentials() {
    return AwsBasicCredentials.create(accessKey, secretKey);
  }

  @Bean
  public S3Client s3Client(AwsCredentials awsCredentials) {

    return S3Client.builder()
        .region(Region.of(region))
        .credentialsProvider(StaticCredentialsProvider.create(awsCredentials))
        .build();
  }
}