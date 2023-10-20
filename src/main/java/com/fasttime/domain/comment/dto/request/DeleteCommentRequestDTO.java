package com.fasttime.domain.comment.dto.request;

import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class DeleteCommentRequestDTO {

    @NotNull(message = "삭제할 댓글 ID를 입력하세요.") Long id;
}
