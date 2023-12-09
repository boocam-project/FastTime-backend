package com.fasttime.domain.article.service;

import com.fasttime.domain.article.entity.Article;
import com.fasttime.domain.article.exception.ArticleNotFoundException;
import com.fasttime.domain.article.repository.ArticleRepository;
import com.fasttime.domain.article.service.usecase.ArticleReportUseCase;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Service
public class ArticleReportService implements ArticleReportUseCase {

    private final ArticleRepository articleRepository;

    public ArticleReportService(ArticleRepository articleRepository) {
        this.articleRepository = articleRepository;
    }

    @Override
    public void reportArticle(ArticleReportServiceRequest request) {
        Article targetArticle = getById(request);
        targetArticle.transToWaitForReview();
    }

    @Override
    public void acceptReport(ArticleReportServiceRequest request) {
        Article targetArticle = getById(request);
        targetArticle.approveReport(request.requestTimeStamp());
    }

    private Article getById(ArticleReportServiceRequest request) {
        return articleRepository.findById(request.articleId())
            .orElseThrow(() -> new ArticleNotFoundException(request.articleId()));
    }

    @Override
    public void rejectReport(ArticleReportServiceRequest request) {
        Article targetArticle = getById(request);
        targetArticle.rejectReport();
    }
}
