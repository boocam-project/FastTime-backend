package com.fasttime.domain.comment.dto.response;

import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class CommentListResponseDTO {

    private int totalPages;
    private Boolean isLastPage;
    private long totalComments;
    private List<CommentResponseDTO> comments;
}
