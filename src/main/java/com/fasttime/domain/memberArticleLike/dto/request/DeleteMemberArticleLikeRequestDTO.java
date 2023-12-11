package com.fasttime.domain.memberArticleLike.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class DeleteMemberArticleLikeRequestDTO {

    @NotNull(message = "게시글 ID를 입력하세요.")
    private Long articleId;
}
