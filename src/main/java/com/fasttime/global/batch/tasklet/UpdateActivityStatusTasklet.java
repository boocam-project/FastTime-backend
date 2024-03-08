package com.fasttime.global.batch.tasklet;

import com.fasttime.domain.reference.entity.Activity;
import com.fasttime.domain.reference.entity.RecruitmentStatus;
import com.fasttime.domain.reference.repository.ActivityRepository;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class UpdateActivityStatusTasklet implements Tasklet {

    private final ActivityRepository activityRepository;

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) {
        log.info("모집이 시작한 대외활동 체크 Tasklet 시작");
        List<Activity> activities = activityRepository.findAllByRecruitmentStart(LocalDate.now());
        for (Activity activity : activities) {
            activity.statusUpdate(RecruitmentStatus.DURING);
        }
        return RepeatStatus.FINISHED;
    }
}
