package com.fasttime.domain.reference.service.usecase;

import com.fasttime.domain.reference.dto.request.ReferenceSearchRequestDto;
import com.fasttime.domain.reference.dto.response.ActivityPageResponseDto;
import com.fasttime.domain.reference.dto.response.ActivityResponseDto;
import com.fasttime.domain.reference.dto.response.CompetitionPageResponseDto;
import com.fasttime.domain.reference.dto.response.CompetitionResponseDto;
import org.springframework.data.domain.Pageable;

public interface ReferenceServiceUseCase {

    ActivityPageResponseDto searchActivities(
        ReferenceSearchRequestDto searchRequestDto,
        Pageable pageable
    );

    CompetitionPageResponseDto searchCompetitions(
        ReferenceSearchRequestDto searchRequestDto,
        Pageable pageable
    );

    ActivityResponseDto getActivity(long activityId);

    CompetitionResponseDto getCompetition(long competitionId);
}
