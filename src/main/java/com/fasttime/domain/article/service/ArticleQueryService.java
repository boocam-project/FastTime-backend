package com.fasttime.domain.article.service;

import com.fasttime.domain.article.dto.service.response.ArticleResponse;
import com.fasttime.domain.article.dto.service.response.ArticlesResponse;
import com.fasttime.domain.article.exception.ArticleNotFoundException;
import com.fasttime.domain.article.repository.ArticleRepository;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
@Service
public class ArticleQueryService implements ArticleQueryUseCase {

    private final ArticleRepository articleRepository;

    public ArticleQueryService(ArticleRepository articleRepository) {
        this.articleRepository = articleRepository;
    }

    @Override
    public ArticleResponse queryById(Long id) {
        return ArticleResponse.entityToDto(articleRepository.findById(id)
            .orElseThrow(() -> new ArticleNotFoundException(
                String.format("Article Not Found From Persistence Layer / articleId = %d", id))));
    }

    @Override
    public List<ArticlesResponse> search(ArticleSearchCondition articleSearchCondition) {
        return articleRepository.search(articleSearchCondition)
            .stream()
            .map(repositoryDto -> ArticlesResponse.builder()
                .id(repositoryDto.getId())
                .title(repositoryDto.getTitle())
                .nickname(repositoryDto.getNickname())
                .anonymity(repositoryDto.isAnonymity())
                .likeCount(repositoryDto.getLikeCount())
                .hateCount(repositoryDto.getHateCount())
                .commentCounts(repositoryDto.getCommentCount())
                .createdAt(repositoryDto.getCreatedAt())
                .lastModifiedAt(repositoryDto.getLastModifiedAt())
                .build())
            .collect(Collectors.toList());
    }
}
