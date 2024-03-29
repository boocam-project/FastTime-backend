package com.fasttime.domain.comment.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CreateCommentRequestDTO {

    @NotBlank(message = "댓글 내용을 입력하세요.")
    @Size(min = 1, max = 100, message = "내용을 100자 이내로 입력하세요.")
    private String content;

    @NotNull(message = "익명 여부를 선택하세요.")
    private Boolean anonymity;

    private Long parentCommentId;
}
