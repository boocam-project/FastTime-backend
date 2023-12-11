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
    private final ArticleRepository postRepository;

    public ArticleCommandService(ArticleSettingProvider articleSettingProvider,
        MemberService memberService, ArticleRepository articleRepository) {
        this.articleSettingProvider = articleSettingProvider;
        this.memberService = memberService;
        this.postRepository = articleRepository;
    }

    @Override
    public ArticleResponse write(ArticleCreateServiceRequest serviceDto) {

        final Member writeMember = memberService.getMember(serviceDto.memberId());
        final Article createdArticle = Article.createNewArticle(writeMember, serviceDto.title(),
            serviceDto.content(), serviceDto.isAnonymity());

        Article savedArticle = postRepository.save(createdArticle);

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
        Article post = findArticleById(serviceDto.articleId());

        isWriter(updateRequestMember, post);
        post.update(serviceDto.title(), serviceDto.content());

        return ArticleResponse.builder()
            .id(post.getId())
            .title(post.getTitle())
            .content(post.getContent())
            .isAnonymity(post.isAnonymity())
            .likeCount(post.getLikeCount())
            .hateCount(post.getHateCount())
            .build();
    }

    @Override
    public void delete(ArticleDeleteServiceRequest serviceDto) {

        final Member deleteRequestMember = memberService.getMember(serviceDto.memberId());
        final Article post = findArticleById(serviceDto.articleId());

        validateAuthority(deleteRequestMember, post);

        post.delete(serviceDto.deletedAt());
    }

    @Override
    public void likeOrHate(ArticleLikeOrHateServiceRequest serviceDto) {
        Article post = findArticleById(serviceDto.articleId());
        post.likeOrHate(serviceDto.isLike(), serviceDto.isIncrease());
    }

    private Article findArticleById(Long postId) {
        return postRepository.findById(postId)
            .orElseThrow(() -> new ArticleNotFoundException(postId));
    }

    private void validateAuthority(Member requestUser, Article post) {
        isWriter(requestUser, post);
    }

    private void isWriter(Member requestMember, Article post) {
        if (!requestMember.getId().equals(post.getMember().getId())) {
            throw new NotArticleWriterException(String.format(
                "This member has no auth to control this post / targetArticleId = %d, requestMemberId = %d",
                post.getId(), requestMember.getId()));
        }
    }
}
