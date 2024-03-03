package com.fasttime.domain.reference.dto.response;

import com.fasttime.domain.reference.entity.Activity;
import com.fasttime.domain.reference.entity.Competition;
import java.time.LocalDate;
import lombok.Builder;

@Builder
public record CompetitionResponseDto(
    String title,
    String organization,
    String corporateType,
    String participate,
    String awardScale,
    LocalDate startDate,
    LocalDate endDate,
    String homepageUrl,
    String activityBenefit,
    String bonusBenefit,
    String description,
    String imageUrl
) {

    public static CompetitionResponseDto of(Competition competition) {
        return CompetitionResponseDto.builder()
            .title(competition.getTitle())
            .organization(competition.getOrganization())
            .corporateType(competition.getCorporateType())
            .participate(competition.getParticipate())
            .awardScale(competition.getAwardScale())
            .startDate(competition.getStartDate())
            .endDate(competition.getEndDate())
            .homepageUrl(competition.getHomepageUrl())
            .activityBenefit(competition.getActivityBenefit())
            .bonusBenefit(competition.getBonusBenefit())
            .description(competition.getDescription())
            .imageUrl(competition.getImageUrl())
            .build();
    }
}
