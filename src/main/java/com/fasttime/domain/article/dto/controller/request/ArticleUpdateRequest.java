package com.fasttime.domain.article.dto.controller.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ArticleUpdateRequest(
    @NotNull Long articleId,
    @NotNull Long memberId,
    boolean isAnonymity,
    @NotBlank String title,
    @NotBlank String content) {
}
