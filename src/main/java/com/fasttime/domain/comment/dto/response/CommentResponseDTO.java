package com.fasttime.domain.comment.dto.response;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class CommentResponseDTO {

    private Long id;
    private Long postId;
    private Long memberId;
    private String nickname;
    private String content;
    private boolean anonymity;
    private Long parentCommentId;
    private String createdAt;
    private String updatedAt;
    private String deletedAt;
}
