package com.fasttime.domain.article.dto.controller.request;

import jakarta.validation.constraints.NotNull;

public record ArticleDeleteRequest(
    @NotNull Long articleId,
    @NotNull Long memberId) {
}
