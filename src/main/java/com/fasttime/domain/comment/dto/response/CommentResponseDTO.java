package com.fasttime.domain.comment.dto.response;

import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class ArticleCommentResponseDTO {

    private Long id;
    private Long memberId;
    private Long postId;
    private String nickname;
    private String content;
    private boolean anonymity;
    private Long parentCommentId;
    private String createdAt;
    private String updatedAt;
    private String deletedAt;
    private List<ArticleCommentResponseDTO> childComments;
}
