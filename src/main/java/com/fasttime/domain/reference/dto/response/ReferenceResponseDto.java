package com.fasttime.domain.reference.dto.response;

import com.fasttime.domain.reference.entity.Activity;
import com.fasttime.domain.reference.entity.Competition;
import lombok.Builder;

@Builder
public record ReferenceResponseDto(
    long id,
    String title,
    String organization,
    String imageUrl
) {

    public static ReferenceResponseDto of(Activity activity) {
        return ReferenceResponseDto.builder()
            .id(activity.getId())
            .title(activity.getTitle())
            .organization(activity.getOrganization())
            .imageUrl(activity.getImageUrl())
            .build();
    }

    public static ReferenceResponseDto of(Competition competition) {
        return ReferenceResponseDto.builder()
            .id(competition.getId())
            .title(competition.getTitle())
            .organization(competition.getOrganization())
            .imageUrl(competition.getImageUrl())
            .build();
    }
}
