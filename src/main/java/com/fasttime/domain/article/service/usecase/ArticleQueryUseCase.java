package com.fasttime.domain.article.service.usecase;

import com.fasttime.domain.article.dto.service.response.ArticleResponse;
import com.fasttime.domain.article.dto.service.response.ArticlesResponse;
import com.fasttime.domain.article.entity.ReportStatus;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

public interface ArticleQueryUseCase {

    ArticleResponse queryById(Long id);

    List<ArticlesResponse> search(ArticlesSearchServiceRequest request);

    List<ArticlesResponse> findReportedArticles(
        ReportedArticlesSearchServiceRequest searchCondition);

    @Getter
    class ArticlesSearchServiceRequest {

        private final String nickname;
        private final String title;
        private final int likeCount;
        private final int pageSize;
        private final int page;

        @Builder
        private ArticlesSearchServiceRequest(String nickname, String title, int likeCount,
            int pageSize,
            int page) {
            this.nickname = nickname;
            this.title = title;
            this.likeCount = likeCount;
            this.pageSize = pageSize;
            this.page = page;
        }
    }

    record ReportedArticlesSearchServiceRequest(int pageNum,
                                                int pageSize,
                                                ReportStatus reportStatus) {

    }
}
