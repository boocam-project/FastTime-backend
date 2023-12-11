package com.fasttime.domain.memberArticleLike.service;

import com.fasttime.domain.article.dto.service.response.ArticleResponse;
import com.fasttime.domain.article.entity.Article;
import com.fasttime.domain.article.service.ArticleCommandService;
import com.fasttime.domain.article.service.ArticleQueryService;
import com.fasttime.domain.article.service.usecase.ArticleCommandUseCase.ArticleLikeOrHateServiceRequest;
import com.fasttime.domain.member.entity.Member;
import com.fasttime.domain.member.service.MemberService;
import com.fasttime.domain.memberArticleLike.dto.MemberArticleLikeDTO;
import com.fasttime.domain.memberArticleLike.dto.request.CreateMemberArticleLikeRequestDTO;
import com.fasttime.domain.memberArticleLike.dto.request.DeleteMemberArticleLikeRequestDTO;
import com.fasttime.domain.memberArticleLike.entity.MemberArticleLike;
import com.fasttime.domain.memberArticleLike.exception.AlreadyExistsMemberArticleLikeException;
import com.fasttime.domain.memberArticleLike.exception.DuplicateMemberArticleLikeException;
import com.fasttime.domain.memberArticleLike.exception.MemberArticleLikeNotFoundException;
import com.fasttime.domain.memberArticleLike.repository.MemberArticleLikeRepository;
import jakarta.transaction.Transactional;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class MemberArticleLikeService {

  private final MemberArticleLikeRepository memberArticleLikeRepository;
  private final ArticleQueryService articleQueryService;
  private final ArticleCommandService articleCommandService;
  private final MemberService memberService;

  public void createMemberArticleLike(
      CreateMemberArticleLikeRequestDTO createMemberArticleLikeRequestDTO, Long memberId) {
    ArticleResponse postResponse =
        articleQueryService.queryById(createMemberArticleLikeRequestDTO.getPostId());
    Member member = memberService.getMember(memberId);
    checkDuplicateMemberArticleLikes(
        member.getId(), postResponse.id(), createMemberArticleLikeRequestDTO.getIsLike());

    memberArticleLikeRepository.save(
        MemberArticleLike.builder()
            .member(member)
            .article(Article.builder().id(postResponse.id()).build())
            .isLike(createMemberArticleLikeRequestDTO.getIsLike())
            .build());

    articleCommandService.likeOrHate(
        new ArticleLikeOrHateServiceRequest(
            postResponse.id(), createMemberArticleLikeRequestDTO.getIsLike(), true));
  }

  public MemberArticleLikeDTO getMemberArticleLike(long memberId, long postId) {
    Optional<MemberArticleLike> memberArticleLike =
        memberArticleLikeRepository.findByMemberIdAndArticleId(memberId, postId);
    return memberArticleLike
        .map(MemberArticleLike::toDTO)
        .orElseGet(this::getFallbackMemberArticleLikeDTO);
  }

  public void deleteMemberArticleLike(DeleteMemberArticleLikeRequestDTO req, Long memberId) {
    MemberArticleLike memberArticleLike =
        memberArticleLikeRepository
            .findByMemberIdAndArticleId(memberId, req.getPostId())
            .orElseThrow(MemberArticleLikeNotFoundException::new);
    memberArticleLikeRepository.delete(memberArticleLike);
  }

  private void checkDuplicateMemberArticleLikes(long memberId, long postId, boolean isLike) {
    memberArticleLikeRepository
        .findByMemberIdAndArticleId(memberId, postId)
        .ifPresent(
            memberArticleLike -> {
              if (memberArticleLike.isLike() == isLike) {
                throw new DuplicateMemberArticleLikeException();
              }
              throw new AlreadyExistsMemberArticleLikeException();
            });
  }
  private MemberArticleLikeDTO getFallbackMemberArticleLikeDTO() {
    return MemberArticleLikeDTO.builder()
        .id(null)
        .memberId(null)
        .postId(null)
        .isLike(null)
        .build();
  }

}
