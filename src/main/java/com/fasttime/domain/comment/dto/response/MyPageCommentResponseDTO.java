package com.fasttime.domain.comment.dto.response;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class MyPageCommentResponseDTO {

    private Long id;
    private Long postId;
    private String nickname;
    private String content;
    private boolean anonymity;
    private Long parentCommentId;
    private String createdAt;
    private String updatedAt;
    private String deletedAt;
}
