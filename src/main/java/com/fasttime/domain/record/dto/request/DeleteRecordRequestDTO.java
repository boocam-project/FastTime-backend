package com.fasttime.domain.record.dto.request;

import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class DeleteRecordRequestDTO {

    @NotNull(message = "게시글 ID를 입력하세요.")
    private Long postId;
}
