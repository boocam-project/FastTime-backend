package com.fasttime.domain.reference.service;

import com.fasttime.domain.reference.dto.request.ReferenceSearchRequestDto;
import com.fasttime.domain.reference.dto.response.ActivityPageResponseDto;
import com.fasttime.domain.reference.dto.response.ActivityResponseDto;
import com.fasttime.domain.reference.dto.response.CompetitionPageResponseDto;
import com.fasttime.domain.reference.dto.response.CompetitionResponseDto;
import com.fasttime.domain.reference.entity.Activity;
import com.fasttime.domain.reference.entity.Competition;
import com.fasttime.domain.reference.exception.ActivityNotFountException;
import com.fasttime.domain.reference.exception.CompetitionNotFountException;
import com.fasttime.domain.reference.repository.ActivityRepository;
import com.fasttime.domain.reference.repository.CompetitionRepository;
import com.fasttime.domain.reference.service.usecase.ReferenceServiceUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ReferenceService implements ReferenceServiceUseCase {

    private final ActivityRepository activityRepository;

    private final CompetitionRepository competitionRepository;

    @Override
    @Transactional(readOnly = true)
    public ActivityPageResponseDto searchActivities(
        ReferenceSearchRequestDto searchRequestDto,
        Pageable pageable
    ) {
        return ActivityPageResponseDto.of(activityRepository.findAllBySearchConditions(
            searchRequestDto,
            pageable
        ));
    }

    @Override
    @Transactional(readOnly = true)
    public CompetitionPageResponseDto searchCompetitions(ReferenceSearchRequestDto searchRequestDto,
        Pageable pageable) {
        return CompetitionPageResponseDto.of(competitionRepository.findAllBySearchConditions(
            searchRequestDto,
            pageable
        ));
    }

    @Override
    @Transactional(readOnly = true)
    public ActivityResponseDto getActivity(long activityId) {
        Activity activity = activityRepository.findById(activityId)
            .orElseThrow(ActivityNotFountException::new);
        return ActivityResponseDto.of(activity);
    }

    @Override
    @Transactional(readOnly = true)
    public CompetitionResponseDto getCompetition(long competitionId) {
        Competition competition = competitionRepository.findById(competitionId)
            .orElseThrow(CompetitionNotFountException::new);
        return CompetitionResponseDto.of(competition);
    }
}
