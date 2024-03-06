package com.fasttime.domain.review.dto.request;

import com.fasttime.domain.member.entity.Member;
import com.fasttime.domain.review.entity.Review;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import java.util.Set;

public record ReviewRequestDTO(
    String title,
    Set<Long> goodtags,
    Set<Long> badtags,
    @Min(1) @Max(5) int rating,
    String content) {

    public Review createReview(Member member) {
        return Review.builder()
            .bootCamp(member.getBootCamp())
            .title(this.title)
            .rating(this.rating)
            .content(this.content)
            .member(member)
            .build();
    }
}
