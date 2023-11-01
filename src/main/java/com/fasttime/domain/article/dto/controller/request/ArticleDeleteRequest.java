package com.fasttime.domain.article.dto.controller.request;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import javax.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class ArticleDeleteRequest {

    @NotNull
    private final Long articleId;

    @NotNull
    private final Long memberId;

    @JsonCreator
    public ArticleDeleteRequest(
        @JsonProperty("articleId") Long articleId,
        @JsonProperty("memberId") Long memberId) {
        this.articleId = articleId;
        this.memberId = memberId;
    }
}
