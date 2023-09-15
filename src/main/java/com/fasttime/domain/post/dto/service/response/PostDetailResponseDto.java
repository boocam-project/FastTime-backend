package com.fasttime.domain.post.dto.service.response;

import lombok.Builder;
import lombok.Getter;

@Getter
public class PostDetailResponseDto {

    private final Long id;
    private final String title;
    private final String content;
    private final boolean anonymity;
    private final int likeCount;
    private final int hateCount;

    @Builder
    private PostDetailResponseDto(Long id, String title, String content, boolean anonymity,
        int likeCount, int hateCount) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.anonymity = anonymity;
        this.likeCount = likeCount;
        this.hateCount = hateCount;
    }
}
