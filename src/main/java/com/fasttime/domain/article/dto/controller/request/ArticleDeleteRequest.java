package com.fasttime.domain.article.dto.controller.request;

import jakarta.validation.constraints.NotNull;

/**
 * articleId는 PathVariable으로, MemberId 는 Spring Security 으로부터 인자로 전달받기 때문에 더 이상 필요하지 않습니다.
 * @param articleId
 * @param memberId
 */
@Deprecated(since = "2023.12.11")
public record ArticleDeleteRequest(
    @NotNull Long articleId,
    @NotNull Long memberId) {
}
