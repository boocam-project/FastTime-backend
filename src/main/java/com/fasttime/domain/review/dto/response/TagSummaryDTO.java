package com.fasttime.domain.review.dto.response;

import java.util.Map;

public record TagSummaryDTO(
    int totalTags,
    Map<Long, Long> tagCounts
) {

}
