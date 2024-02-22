package com.fasttime.domain.review.unit.entity;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasttime.domain.review.entity.ReviewTag;
import com.fasttime.domain.review.entity.Tag;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class TagTest {

    @DisplayName("Tag 엔터티를 생성할 수 있다.")
    @Test
    void create_Tag_willSuccess() {
        // given
        String content = "태그 내용";

        // when
        Tag tag = Tag.create(content);

        // then
        assertThat(tag.getContent()).isEqualTo(content);
    }

    @DisplayName("Tag 엔터티에 ReviewTag를 연결할 수 있다.")
    @Test
    void associate_ReviewTag_willSuccess() {
        // given
        Tag tag = Tag.create("태그 내용");
        ReviewTag reviewTag = new ReviewTag();

        // when
        tag.getReviewTags().add(reviewTag);

        // then
        assertThat(tag.getReviewTags()).contains(reviewTag);
    }
}
