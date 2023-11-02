package com.fasttime.domain.comment.dto.response;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class CommentResponseDTO {

    private long commentId;
    private long memberId;
    private long articleId;
    private String nickname;
    private String content;
    private Boolean anonymity;
    private long parentCommentId;
    private String createdAt;
    private String updatedAt;
    private String deletedAt;
    private int childCommentCount;
}
