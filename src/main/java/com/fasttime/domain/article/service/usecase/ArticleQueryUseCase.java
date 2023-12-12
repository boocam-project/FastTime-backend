package com.fasttime.domain.article.service.usecase;

import com.fasttime.domain.article.dto.service.response.ArticleResponse;
import com.fasttime.domain.article.dto.service.response.ArticlesResponse;
import com.fasttime.domain.article.entity.ReportStatus;
import java.util.List;
import lombok.Builder;

public interface ArticleQueryUseCase {

    ArticleResponse queryById(Long id);

    List<ArticlesResponse> search(ArticlesSearchRequestServiceDto request);

    List<ArticlesResponse> findReportedArticles(ReportedArticlesSearchRequestServiceDto request);

    @Builder
    record ArticlesSearchRequestServiceDto(
        String nickname,
        String title,
        int likeCount,
        int pageSize,
        int page
    ) {

    }

    record ReportedArticlesSearchRequestServiceDto(
        int pageNum,
        int pageSize,
        ReportStatus reportStatus) {

    }
}
