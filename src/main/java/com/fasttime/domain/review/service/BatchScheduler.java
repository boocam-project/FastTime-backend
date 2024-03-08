package com.fasttime.domain.review.service;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecutionException;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@EnableScheduling
public class BatchScheduler {

    private final JobLauncher jobLauncher;
    private final Job deleteOldReviewsJob;

    public BatchScheduler(JobLauncher jobLauncher, Job deleteOldReviewsJob) {
        this.jobLauncher = jobLauncher;
        this.deleteOldReviewsJob = deleteOldReviewsJob;
    }

    @Scheduled(cron = "0 0 3 * * *")
    public void runDeleteOldReviewsJob() throws JobExecutionException {
        jobLauncher.run(deleteOldReviewsJob, new JobParameters());
    }
}
