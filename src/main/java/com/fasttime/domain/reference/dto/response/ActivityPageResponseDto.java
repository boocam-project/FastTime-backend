package com.fasttime.domain.reference.dto.response;

import com.fasttime.domain.reference.entity.Activity;
import java.util.ArrayList;
import java.util.List;
import lombok.Builder;
import org.springframework.data.domain.Page;

@Builder
public record ActivityPageResponseDto(
    int totalPages,
    Boolean isLastPage,
    long totalActivities,
    List<ReferenceResponseDto> activities
) {

    public static ActivityPageResponseDto of(Page<Activity> activityPage) {
        List<ReferenceResponseDto> activities = new ArrayList<>();
        for(Activity activity : activityPage.getContent()) {
            activities.add(ReferenceResponseDto.of(activity));
        }
        return ActivityPageResponseDto.builder()
            .totalPages(activityPage.getTotalPages())
            .isLastPage(activityPage.isLast())
            .totalActivities(activityPage.getTotalElements())
            .activities(activities)
            .build();
    }
}
