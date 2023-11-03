package com.fasttime.domain.comment.dto.request;

import lombok.Builder;
import lombok.Getter;

@Getter
public class GetCommentsRequestDTO {

    private final Long articleId;
    private final Long memberId;
    private final Long parentCommentId;
    private final int pageSize;
    private final int page;

    @Builder
    private GetCommentsRequestDTO(Long articleId, Long memberId, Long parentCommentId, int pageSize,
        int page) {
        this.articleId = articleId;
        this.memberId = memberId;
        this.parentCommentId = parentCommentId;
        this.pageSize = pageSize;
        this.page = page;
    }
}
