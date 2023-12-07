package com.fasttime.domain.comment.dto.request;

import lombok.Builder;
import lombok.Getter;

@Getter
public class GetCommentsRequestDTO {

    private final Long articleId;
    private final Long memberId;
    private final Long parentCommentId;

    @Builder
    private GetCommentsRequestDTO(Long articleId, Long memberId, Long parentCommentId) {
        this.articleId = articleId;
        this.memberId = memberId;
        this.parentCommentId = parentCommentId;
    }
}
