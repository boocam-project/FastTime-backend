package com.fasttime.global.batch.scheduler;

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
    private final Job updateReferenceJob;
    private final Job updateReferenceStatusJob;

    public BatchScheduler(JobLauncher jobLauncher, Job deleteOldReviewsJob, Job updateReferenceJob) {
        this.jobLauncher = jobLauncher;
        this.deleteOldReviewsJob = deleteOldReviewsJob;
        this.updateReferenceJob = updateReferenceJob;
        this.updateReferenceStatusJob = updateReferenceJob;
    }

    @Scheduled(cron = "0 0 3 * * *")
    public void runDeleteOldReviewsJob() throws JobExecutionException {
        jobLauncher.run(deleteOldReviewsJob, new JobParameters());
    }

    @Scheduled(cron = "0 0 2 * * *")
    public void runUpdateReferenceStatusJob() throws JobExecutionException {
        jobLauncher.run(updateReferenceStatusJob, new JobParameters());
    }

    @Scheduled(cron = "0 30 2 * * *")
    public void runUpdateReferenceJob() throws JobExecutionException {
        jobLauncher.run(updateReferenceJob, new JobParameters());
    }
}
