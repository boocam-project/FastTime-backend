package com.fasttime.domain.comment.dto;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class CommentDTO {

    private Long id;
    private Long postId;
    private Long memberId;
    private String content;
    private boolean anonymity;
    private Long parentCommentId;
    private String createdAt;
    private String updatedAt;
    private String deletedAt;
}
