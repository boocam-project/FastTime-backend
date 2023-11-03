package com.fasttime.domain.article.service.usecase;

import java.time.LocalDateTime;
import lombok.Getter;

public interface ArticleReportUseCase {

    void reportArticle(ArticleReportServiceRequest request);

    void acceptReport(ArticleReportServiceRequest request);

    void rejectReport(ArticleReportServiceRequest request);

    @Getter
    class ArticleReportServiceRequest {

        private final Long articleId;
        private final LocalDateTime requestTimeStamp;

        public ArticleReportServiceRequest(Long articleId, LocalDateTime requestTimeStamp) {
            this.articleId = articleId;
            this.requestTimeStamp = requestTimeStamp;
        }
    }
}
