package com.fasttime.domain.reference.dto.response;

import com.fasttime.domain.reference.entity.Activity;
import java.util.List;
import lombok.Builder;
import org.springframework.data.domain.Page;

@Builder
public record ActivityPageResponseDto(
    int totalPages,
    Boolean isLastPage,
    long totalActivity,
    List<Activity> activities
) {

    public static ActivityPageResponseDto of(Page<Activity> activityPage) {
        return ActivityPageResponseDto.builder()
            .totalPages(activityPage.getTotalPages())
            .isLastPage(activityPage.isLast())
            .totalActivity(activityPage.getTotalElements())
            .activities(activityPage.getContent())
            .build();
    }
}
