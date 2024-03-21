package com.fasttime.global.batch.tasklet;

import com.fasttime.domain.reference.entity.Competition;
import com.fasttime.domain.reference.entity.RecruitmentStatus;
import com.fasttime.domain.reference.repository.CompetitionRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
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
public class UpdateCompetitionStatusTasklet implements Tasklet {

    private final CompetitionRepository competitionRepository;


    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) {
        log.info("모집이 시작한 공모전 체크 Tasklet 시작");
        List<Competition> competitions = competitionRepository.findAllByRecruitmentStart(
            LocalDate.now());
        for (Competition competition : competitions) {
            competition.statusUpdate(RecruitmentStatus.DURING);
        }

        return RepeatStatus.FINISHED;
    }
}