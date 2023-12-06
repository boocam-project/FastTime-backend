package com.fasttime.domain.report.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CreateReportRequestDTO {

    @NotNull(message = "게시글 ID를 입력하세요.")
    private Long postId;
}
