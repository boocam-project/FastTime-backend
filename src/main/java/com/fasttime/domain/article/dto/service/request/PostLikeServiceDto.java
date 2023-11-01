package com.fasttime.domain.article.dto.service.request;

import lombok.Getter;

@Getter
public class PostLikeServiceDto {

    private final Long postId;
    private final Long memberId;
    private final boolean isLike;

    public PostLikeServiceDto(Long postId, Long memberId, boolean isLike) {
        this.postId = postId;
        this.memberId = memberId;
        this.isLike = isLike;
    }
}
