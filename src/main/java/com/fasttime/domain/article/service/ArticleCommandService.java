package com.fasttime.domain.article.service;

import com.fasttime.domain.article.dto.service.response.ArticleResponse;
import com.fasttime.domain.article.entity.Article;
import com.fasttime.domain.article.exception.ArticleNotFoundException;
import com.fasttime.domain.article.exception.NotArticleWriterException;
import com.fasttime.domain.article.repository.ArticleRepository;
import com.fasttime.domain.article.service.usecase.ArticleCommandUseCase;
import com.fasttime.domain.member.entity.Member;
import com.fasttime.domain.member.service.MemberService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Service
public class ArticleCommandService implements ArticleCommandUseCase {

    private final ArticleSettingProvider articleSettingProvider;
    private final MemberService memberService;
    private final ArticleRepository articleRepository;

    public ArticleCommandService(ArticleSettingProvider articleSettingProvider,
        MemberService memberService, ArticleRepository articleRepository) {
        this.articleSettingProvider = articleSettingProvider;
        this.memberService = memberService;
        this.articleRepository = articleRepository;
    }

    @Override
    public ArticleResponse write(ArticleCreateServiceRequest serviceDto) {

        final Member writeMember = memberService.getMember(serviceDto.memberId());
        final Article createdArticle = Article.createNewArticle(writeMember, serviceDto.title(),
            serviceDto.content(), serviceDto.isAnonymity());

        Article savedArticle = articleRepository.save(createdArticle);

        return ArticleResponse.builder()
            .id(savedArticle.getId())
            .title(savedArticle.getTitle())
            .content(savedArticle.getContent())
            .nickname(savedArticle.isAnonymity() ? articleSettingProvider.getAnonymousNickname()
                : savedArticle.getMember().getNickname())
            .isAnonymity(savedArticle.isAnonymity())
            .likeCount(savedArticle.getLikeCount())
            .hateCount(savedArticle.getHateCount())
            .createdAt(savedArticle.getCreatedAt())
            .lastModifiedAt(savedArticle.getUpdatedAt())
            .build();
    }

    @Override
    public ArticleResponse update(ArticleUpdateServiceRequest serviceDto) {

        final Member updateRequestMember = memberService.getMember(serviceDto.memberId());
        Article article = findArticleById(serviceDto.articleId());

        isWriter(updateRequestMember, article);
        article.update(serviceDto.title(), serviceDto.content());

        return ArticleResponse.builder()
            .id(article.getId())
            .title(article.getTitle())
            .content(article.getContent())
            .isAnonymity(article.isAnonymity())
            .likeCount(article.getLikeCount())
            .hateCount(article.getHateCount())
            .build();
    }

    @Override
    public void delete(ArticleDeleteServiceRequest serviceDto) {

        final Member deleteRequestMember = memberService.getMember(serviceDto.memberId());
        final Article article = findArticleById(serviceDto.articleId());

        validateAuthority(deleteRequestMember, article);

        article.delete(serviceDto.deletedAt());
    }

    @Override
    public void likeOrHate(ArticleLikeOrHateServiceRequest serviceDto) {
        Article article = findArticleById(serviceDto.articleId());
        article.likeOrHate(serviceDto.isLike(), serviceDto.isIncrease());
    }

    private Article findArticleById(Long articleId) {
        return articleRepository.findById(articleId)
            .orElseThrow(() -> new ArticleNotFoundException(articleId));
    }

    private void validateAuthority(Member requestUser, Article article) {
        isWriter(requestUser, article);
    }

    private void isWriter(Member requestMember, Article article) {
        if (!requestMember.getId().equals(article.getMember().getId())) {
            throw new NotArticleWriterException(String.format(
                "This member has no auth to control this article / targetArticleId = %d, requestMemberId = %d",
                article.getId(), requestMember.getId()));
        }
    }
}
