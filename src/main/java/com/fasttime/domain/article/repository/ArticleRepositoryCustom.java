package com.fasttime.domain.article.repository;

import com.fasttime.domain.article.service.usecase.ArticleQueryUseCase.ArticlesSearchRequestServiceDto;
import java.time.LocalDateTime;
import java.util.List;

public interface ArticleRepositoryCustom {

    List<ArticleQueryResponse> search(ArticlesSearchRequestServiceDto searchCondition);

    record ArticleQueryResponse(
        Long id,
        Long memberId,
        String nickname,
        String title,
        boolean isAnonymity,
        int commentCount,
        int likeCount,
        int hateCount,
        LocalDateTime createdAt,
        LocalDateTime lastModifiedAt,
        LocalDateTime deletedAt
    ) {
    }
}
