package com.fasttime.domain.review.dto.response;

import java.util.Map;

public record BootcampReviewSummaryDTO(
    String bootcamp,
    double averageRating,
    int totalReviews,
    int totalTags,
    Map<Long, Long> tagCounts
) {}
