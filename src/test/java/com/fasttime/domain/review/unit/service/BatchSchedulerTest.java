package com.fasttime.domain.review.unit.service;

import com.fasttime.global.batch.scheduler.BatchScheduler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.launch.JobLauncher;

import static org.mockito.Mockito.*;

class BatchSchedulerTest {

    @Mock
    private JobLauncher jobLauncher;
    @Mock
    private Job deleteOldReviewsJob;

    @InjectMocks
    private BatchScheduler batchScheduler;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void runDeleteOldReviewsJob() throws Exception {
        when(jobLauncher.run(deleteOldReviewsJob, new JobParameters())).thenReturn(
            new JobExecution(0L));

        batchScheduler.runDeleteOldReviewsJob();

        verify(jobLauncher).run(deleteOldReviewsJob, new JobParameters());
    }
}
