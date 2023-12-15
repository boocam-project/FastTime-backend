package com.fasttime.domain.article.dto.controller.request;

import jakarta.validation.constraints.NotBlank;

public record ArticleUpdateRequestV2(
    @NotBlank String title,
    @NotBlank String content,
    boolean isAnonymity
) {
}
