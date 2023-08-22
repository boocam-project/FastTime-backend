package com.fasttime.domain.comment.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
public class CommentDto {

    private Long id;
    private Long postId;
    private Long memberId;
    private String content;
    private boolean anonymity;
    private Long parentCommentId;
}
