package com.fasttime.domain.article.dto.controller.request;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class ArticleUpdateRequest {

    @NotNull
    private final Long articleId;

    @NotNull
    private final Long memberId;

    @NotBlank
    private final String title;

    @NotBlank
    private final String content;

    @JsonCreator
    public ArticleUpdateRequest(
        @JsonProperty("articleId") Long articleId,
        @JsonProperty("memberId") Long memberId,
        @JsonProperty("title") String title,
        @JsonProperty("content") String content) {
        this.articleId = articleId;
        this.memberId = memberId;
        this.title = title;
        this.content = content;
    }
}