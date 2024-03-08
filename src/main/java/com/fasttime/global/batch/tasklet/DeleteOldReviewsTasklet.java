package com.fasttime.global.batch.tasklet;

import com.fasttime.domain.review.repository.ReviewRepository;
import java.time.LocalDateTime;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.batch.core.step.tasklet.Tasklet;

public class DeleteOldReviewsTasklet implements Tasklet {

    private final ReviewRepository reviewRepository;

    public DeleteOldReviewsTasklet(ReviewRepository reviewRepository) {
        this.reviewRepository = reviewRepository;
    }

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) {

        LocalDateTime cutoffDate = LocalDateTime.now().minusDays(7);
        reviewRepository.deleteReviewsOlderThan7Days(cutoffDate);

        return RepeatStatus.FINISHED;
    }
}
