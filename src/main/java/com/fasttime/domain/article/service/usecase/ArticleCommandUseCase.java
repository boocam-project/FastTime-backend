package com.fasttime.domain.article.service.usecase;

import com.fasttime.domain.article.dto.service.response.ArticleResponse;
import java.time.LocalDateTime;

public interface ArticleCommandUseCase {

    ArticleResponse write(ArticleCreateServiceRequest request);

    ArticleResponse update(ArticleUpdateServiceRequest request);

    void delete(ArticleDeleteServiceRequest request);

    void likeOrHate(ArticleLikeOrHateServiceRequest request);

    record ArticleCreateServiceRequest(
        Long memberId,
        String title,
        String content,
        boolean isAnonymity) {

    }

    record ArticleUpdateServiceRequest(
        Long articleId,
        Long memberId,
        String title,
        boolean isAnonymity,
        String content) {

    }

    record ArticleDeleteServiceRequest(
        Long articleId,
        Long memberId,
        LocalDateTime deletedAt) {

    }

    record ArticleLikeOrHateServiceRequest(
        Long articleId,
        boolean isLike,
        boolean isIncrease
    ) {

    }

}
