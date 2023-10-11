package com.fasttime.domain.comment.dto.response;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class PostCommentResponseDTO {

    private Long id;
    private Long memberId;
    private String nickname;
    private String content;
    private boolean anonymity;
    private Long parentCommentId;
    private String createdAt;
    private String updatedAt;
    private String deletedAt;
}
