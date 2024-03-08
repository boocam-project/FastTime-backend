package com.fasttime.global.config;

import com.fasttime.domain.review.repository.ReviewRepository;
import com.fasttime.domain.review.service.DeleteOldReviewsTasklet;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.batch.core.repository.JobRepository;

@Configuration
@EnableBatchProcessing
public class BatchConfig {

    @Bean
    public Job deleteOldReviewsJob(JobRepository jobRepository, Step deleteOldReviewsStep) {
        return new JobBuilder("deleteOldReviewsJob", jobRepository)
            .start(deleteOldReviewsStep)
            .build();
    }

    @Bean
    public Step deleteOldReviewsStep(JobRepository jobRepository,
        PlatformTransactionManager transactionManager, DeleteOldReviewsTasklet tasklet) {
        return new StepBuilder("deleteOldReviewsStep", jobRepository)
            .tasklet(tasklet, transactionManager)
            .build();
    }

    @Bean
    public DeleteOldReviewsTasklet deleteOldReviewsTasklet(ReviewRepository reviewRepository) {
        return new DeleteOldReviewsTasklet(reviewRepository);
    }
}
