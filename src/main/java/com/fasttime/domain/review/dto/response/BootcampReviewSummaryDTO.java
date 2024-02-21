package com.fasttime.domain.review.dto.response;

public record BootcampReviewSummaryDTO(
    String bootcamp,
    double averageRating,
    int totalReviews
) {}
