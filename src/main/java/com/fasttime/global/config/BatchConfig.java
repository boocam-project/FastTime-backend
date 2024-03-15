package com.fasttime.global.config;

import com.fasttime.domain.review.repository.ReviewRepository;
import com.fasttime.global.batch.tasklet.DeleteOldReviewsTasklet;
import com.fasttime.global.batch.tasklet.UpdateActivityStatusTasklet;
import com.fasttime.global.batch.tasklet.UpdateCompetitionStatusTasklet;
import com.fasttime.global.batch.tasklet.UpdateDoneActivityTasklet;
import com.fasttime.global.batch.tasklet.UpdateDoneCompetitionTasklet;
import com.fasttime.global.batch.tasklet.UpdateNewActivityTasklet;
import com.fasttime.global.batch.tasklet.UpdateNewCompetitionTasklet;
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

    @Bean
    public Job updateReferenceStatusJob(JobRepository jobRepository, Step updateActivityStatusStep,
        Step updateCompetitionStatusStep) {
        return new JobBuilder("updateReferenceStatusJob", jobRepository)
            .start(updateActivityStatusStep)
            .next(updateCompetitionStatusStep)
            .build();
    }

    @Bean
    public Job updateReferenceJob(JobRepository jobRepository, Step updateNewActivityStep,
        Step updateNewCompetitionStep, Step updateDoneActivityStep, Step updateDoneCompetitionStep) {
        return new JobBuilder("updateReferenceJob", jobRepository)
            .start(updateNewActivityStep)
            .next(updateNewCompetitionStep)
            .next(updateDoneActivityStep)
            .next(updateDoneCompetitionStep)
            .build();
    }

    @Bean
    public Step updateActivityStatusStep(JobRepository jobRepository,
        PlatformTransactionManager transactionManager, UpdateActivityStatusTasklet tasklet){
        return new StepBuilder("updateActivityStatusStep", jobRepository)
            .tasklet(tasklet,transactionManager)
            .build();
    }

    @Bean
    public Step updateCompetitionStatusStep(JobRepository jobRepository,
        PlatformTransactionManager transactionManager, UpdateCompetitionStatusTasklet tasklet){
        return new StepBuilder("updateCompetitionStatusStep", jobRepository)
            .tasklet(tasklet,transactionManager)
            .build();
    }

    @Bean
    public Step updateNewActivityStep(JobRepository jobRepository,
        PlatformTransactionManager transactionManager, UpdateNewActivityTasklet tasklet){
        return new StepBuilder("updateNewActivityStep", jobRepository)
            .tasklet(tasklet,transactionManager)
            .build();
    }

    @Bean
    public Step updateNewCompetitionStep(JobRepository jobRepository,
        PlatformTransactionManager transactionManager, UpdateNewCompetitionTasklet tasklet){
        return new StepBuilder("updateNewCompetitionStep", jobRepository)
            .tasklet(tasklet,transactionManager)
            .build();
    }

    @Bean
    public Step updateDoneActivityStep(JobRepository jobRepository,
        PlatformTransactionManager transactionManager, UpdateDoneActivityTasklet tasklet){
        return new StepBuilder("updateDoneActivityStep", jobRepository)
            .tasklet(tasklet,transactionManager)
            .build();
    }

    @Bean
    public Step updateDoneCompetitionStep(JobRepository jobRepository,
        PlatformTransactionManager transactionManager, UpdateDoneCompetitionTasklet tasklet){
        return new StepBuilder("updateDoneCompetitionStep", jobRepository)
            .tasklet(tasklet,transactionManager)
            .build();
    }
}
