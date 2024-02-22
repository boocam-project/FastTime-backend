package com.fasttime.domain.review.unit.entity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.fasttime.domain.review.entity.Review;
import com.fasttime.domain.review.entity.ReviewTag;
import com.fasttime.domain.member.entity.Member;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class ReviewTest {

    @DisplayName("리뷰를 생성할 수 있다.")
    @Test
    void create_review_willSuccess() {
        // given
        String title = "제목";
        String content = "내용";
        String bootcamp = "부트캠프";
        int rating = 5;

        // when
        Review review = Review.builder()
            .title(title)
            .content(content)
            .bootcamp(bootcamp)
            .rating(rating)
            .member(new Member())
            .build();

        // then
        assertThat(review).extracting("title", "content", "bootcamp", "rating")
            .containsExactly(title, content, bootcamp, rating);
    }

    @DisplayName("리뷰 내용을 수정할 수 있다.")
    @Test
    void update_review_willSuccess() {
        // given
        Review review = Review.builder()
            .title("원래 제목")
            .content("원래 내용")
            .bootcamp("부트캠프")
            .rating(5)
            .build();
        String updatedTitle = "수정된 제목";
        String updatedContent = "수정된 내용";
        int updatedRating = 4;

        // when
        review.updateReviewDetails(updatedTitle, updatedRating, updatedContent);

        // then
        assertThat(review).extracting("title", "content", "rating")
            .containsExactly(updatedTitle, updatedContent, updatedRating);
    }

    @DisplayName("리뷰에 태그를 설정할 수 있다.")
    @Test
    void set_reviewTags_willSuccess() {
        // given
        Review review = Review.builder().build();
        Set<ReviewTag> tags = new HashSet<>();
        tags.add(new ReviewTag());

        // when
        review.setReviewTags(tags);

        // then
        assertTrue(review.getReviewTags().containsAll(tags));
    }

    @DisplayName("리뷰를 삭제할 수 있다.")
    @Test
    void delete_review_willSuccess() {
        // given
        Review review = Review.builder().build();

        // when
        review.softDelete();

        // then
        assertTrue(review.isDeleted());
    }

    @DisplayName("리뷰를 복구할 수 있다.")
    @Test
    void restore_willSuccess() {
        // given
        Review review = Review.builder().build();
        review.softDelete();

        // when
        review.restore();

        // then
        assertTrue(!review.isDeleted());
    }
}
