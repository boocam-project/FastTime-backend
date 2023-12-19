package com.fasttime.domain.comment.infra;

public record CommentCreateEvent(
    Long commentId,
    Long articleId
) {

}
