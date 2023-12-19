package com.fasttime.domain.comment.infra;

public record CommentDeleteEvent(
    Long commentId,
    Long articleId
) {

}
