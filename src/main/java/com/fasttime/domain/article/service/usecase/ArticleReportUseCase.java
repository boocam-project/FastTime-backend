package com.fasttime.domain.article.service.usecase;

import java.time.LocalDateTime;

public interface ArticleReportUseCase {

    void reportArticle(ArticleReportServiceRequest request);

    void acceptReport(ArticleReportServiceRequest request);

    void rejectReport(ArticleReportServiceRequest request);

    record ArticleReportServiceRequest(
        Long articleId,
        LocalDateTime requestTimeStamp) {

    }
}
