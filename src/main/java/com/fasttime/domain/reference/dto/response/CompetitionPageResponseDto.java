package com.fasttime.domain.reference.dto.response;

import com.fasttime.domain.reference.entity.Competition;
import java.util.ArrayList;
import java.util.List;
import lombok.Builder;
import org.springframework.data.domain.Page;

@Builder
public record CompetitionPageResponseDto(
    int totalPages,
    Boolean isLastPage,
    long totalCompetitions,
    List<ReferenceResponseDto> competitions
) {

    public static CompetitionPageResponseDto of(Page<Competition> competitionPage) {
        List<ReferenceResponseDto> competitions = new ArrayList<>();
        for (Competition competition : competitionPage.getContent()) {
            competitions.add(ReferenceResponseDto.of(competition));
        }
        return CompetitionPageResponseDto.builder()
            .totalPages(competitionPage.getTotalPages())
            .isLastPage(competitionPage.isLast())
            .totalCompetitions(competitionPage.getTotalElements())
            .competitions(competitions)
            .build();
    }
}
