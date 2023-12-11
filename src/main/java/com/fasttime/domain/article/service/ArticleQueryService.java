package com.fasttime.domain.article.service;

import com.fasttime.domain.article.dto.service.response.ArticleResponse;
import com.fasttime.domain.article.dto.service.response.ArticlesResponse;
import com.fasttime.domain.article.entity.Article;
import com.fasttime.domain.article.exception.ArticleNotFoundException;
import com.fasttime.domain.article.repository.ArticleRepository;
import com.fasttime.domain.article.service.usecase.ArticleQueryUseCase;
import java.util.List;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
@Service
public class ArticleQueryService implements ArticleQueryUseCase {

    private final ArticleSettingProvider articleSettingProvider;
    private final ArticleRepository articleRepository;

    public ArticleQueryService(ArticleSettingProvider articleSettingProvider,
        ArticleRepository articleRepository) {
        this.articleSettingProvider = articleSettingProvider;
        this.articleRepository = articleRepository;
    }

    @Override
    public ArticleResponse queryById(Long id) {
        Article targetArticle = articleRepository.findById(id)
            .orElseThrow(() -> new ArticleNotFoundException(id));

        return ArticleResponse.builder()
            .id(targetArticle.getId())
            .title(targetArticle.getTitle())
            .content(targetArticle.getContent())
            .nickname(targetArticle.isAnonymity() ?
                articleSettingProvider.getAnonymousNickname()
                : targetArticle.getMember().getNickname())
            .isAnonymity(targetArticle.isAnonymity())
            .likeCount(targetArticle.getLikeCount())
            .hateCount(targetArticle.getHateCount())
            .createdAt(targetArticle.getCreatedAt())
            .lastModifiedAt(targetArticle.getUpdatedAt())
            .build();
    }

    @Override
    public List<ArticlesResponse> search(ArticlesSearchRequestServiceDto request) {
        return articleRepository.search(request)
            .stream()
            .map(repositoryDto -> ArticlesResponse.builder()
                .id(repositoryDto.id())
                .title(repositoryDto.title())
                .nickname(
                    repositoryDto.isAnonymity() ? articleSettingProvider.getAnonymousNickname()
                        : repositoryDto.nickname())
                .isAnonymity(repositoryDto.isAnonymity())
                .likeCount(repositoryDto.likeCount())
                .hateCount(repositoryDto.hateCount())
                .commentCounts(repositoryDto.commentCount())
                .createdAt(repositoryDto.createdAt())
                .lastModifiedAt(repositoryDto.lastModifiedAt())
                .build())
            .toList();
    }

    @Override
    public List<ArticlesResponse> findReportedArticles(
        ReportedArticlesSearchRequestServiceDto request) {
        return articleRepository
            .findAllByReportStatus(cretePageRequest(request), request.reportStatus())
            .stream()
            .map(result -> ArticlesResponse.builder()
                .id(result.getId())
                .title(result.getTitle())
                .likeCount(result.getLikeCount())
                .hateCount(result.getHateCount())
                .createdAt(result.getCreatedAt())
                .lastModifiedAt(result.getCreatedAt())
                .build())
            .toList();
    }

    private PageRequest cretePageRequest(ReportedArticlesSearchRequestServiceDto request) {
        return PageRequest.of(request.pageNum(), request.pageSize(),
            Sort.by(articleSettingProvider.getDefaultOrderField()).descending());
    }
}
