package com.fasttime.domain.review.dto.response;

import com.fasttime.domain.review.entity.Review;
import java.util.Set;

public record ReviewResponseDTO(
    Long id,
    String authorNickname,
    String bootcamp,
    String title,
    Set<String> goodtags,
    Set<String> badtags,
    int rating,
    String content) {

    public static ReviewResponseDTO of(Review review, Set<String> goodTagContents,
        Set<String> badTagContents) {
        return new ReviewResponseDTO(
            review.getId(),
            review.getMember().getNickname(),
            review.getBootCamp().getName(),
            review.getTitle(),
            goodTagContents,
            badTagContents,
            review.getRating(),
            review.getContent());
    }
}
