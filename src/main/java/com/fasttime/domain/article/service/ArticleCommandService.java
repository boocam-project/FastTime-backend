package com.fasttime.domain.article.service;

import com.fasttime.domain.member.entity.Member;
import com.fasttime.domain.member.service.MemberService;
import com.fasttime.domain.article.dto.service.response.ArticleResponse;
import com.fasttime.domain.article.entity.Article;
import com.fasttime.domain.article.exception.NotArticleWriterException;
import com.fasttime.domain.article.exception.ArticleNotFoundException;
import com.fasttime.domain.article.repository.ArticleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Service
public class ArticleCommandService implements ArticleCommandUseCase {

    private final MemberService memberService;
    private final ArticleRepository postRepository;

    public ArticleCommandService(MemberService memberService, ArticleRepository postRepository) {
        this.memberService = memberService;
        this.postRepository = postRepository;
    }

    @Override
    public ArticleResponse write(ArticleCreateServiceRequest serviceDto) {

        final Member writeMember = memberService.getMember(serviceDto.getMemberId());
        final Article createdArticle = Article.createNewArticle(writeMember, serviceDto.getTitle(),
            serviceDto.getContent(), serviceDto.isAnonymity());

        return ArticleResponse.entityToDto(postRepository.save(createdArticle));
    }

    @Override
    public ArticleResponse update(ArticleUpdateServiceRequest serviceDto) {

        final Member updateRequestMember = memberService.getMember(serviceDto.getMemberId());
        Article post = findArticleById(serviceDto.getArticleId());

        isWriter(updateRequestMember, post);

        post.update(serviceDto.getTitle(), serviceDto.getContent());

        return ArticleResponse.builder()
            .id(post.getId())
            .title(post.getTitle())
            .content(post.getContent())
            .anonymity(post.isAnonymity())
            .likeCount(post.getLikeCount())
            .hateCount(post.getHateCount())
            .build();
    }

    @Override
    public void delete(ArticleDeleteServiceRequest serviceDto) {

        final Member deleteRequestMember = memberService.getMember(serviceDto.getMemberId());
        final Article post = findArticleById(serviceDto.getArticleId());

        validateAuthority(deleteRequestMember, post);

        post.delete(serviceDto.getDeletedAt());
    }

    @Override
    public void likeOrHate(ArticleLikeOrHateServiceRequest serviceDto) {
        Article post = findArticleById(serviceDto.getArticleId());
        post.likeOrHate(serviceDto.isLike(), serviceDto.isIncrease());
    }

    private Article findArticleById(Long postId) {
        return postRepository.findById(postId)
            .orElseThrow(() -> new ArticleNotFoundException(
                String.format("Article Not Found From Persistence Layer / postId = %d", postId)));
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
