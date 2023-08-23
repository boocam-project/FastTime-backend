package com.fasttime.domain.post.dto.service.request;

import lombok.Getter;

@Getter
public class PostUpdateServiceDto {

    private final Long postId;
    private final Long memberId;
    private final String content;

    public PostUpdateServiceDto(Long postId, Long memberId, String content) {
        this.postId = postId;
        this.memberId = memberId;
        this.content = content;
    }
}
