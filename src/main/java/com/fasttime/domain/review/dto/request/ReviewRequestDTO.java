package com.fasttime.domain.review.dto.request;

import com.fasttime.domain.member.entity.Member;
import com.fasttime.domain.review.entity.Review;
import java.util.Set;

public record ReviewRequestDTO(
    String title,
    Set<Long> goodtags,
    Set<Long> badtags,
    int rating,
    String content) {
    public Review createReview(Member member) {
        return Review.builder()
            .bootcamp(member.getBootcamp())
            .title(this.title)
            .rating(this.rating)
            .content(this.content)
            .member(member)
            .build();
    }
}
