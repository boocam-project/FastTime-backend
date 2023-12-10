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
    private final ArticleQueryService postQueryService;
    private final ArticleCommandService postCommandService;
    private final MemberService memberService;

    public void createRecord(CreateMemberArticleLikeRequestDTO createMemberArticleLikeRequestDTO, Long memberId) {
        ArticleResponse postResponse = postQueryService.queryById(
            createMemberArticleLikeRequestDTO.getPostId());
        Member member = memberService.getMember(memberId);
        checkDuplicateRecords(member.getId(), postResponse.id(),
            createMemberArticleLikeRequestDTO.getIsLike());

        memberArticleLikeRepository.save(
            MemberArticleLike.builder().member(member).article(Article.builder().id(postResponse.id()).build())
                .isLike(createMemberArticleLikeRequestDTO.getIsLike()).build());

        postCommandService.likeOrHate(new ArticleLikeOrHateServiceRequest(postResponse.id(),
            createMemberArticleLikeRequestDTO.getIsLike(), true));
    }

    public MemberArticleLikeDTO getRecord(long memberId, long postId) {
        Optional<MemberArticleLike> record = memberArticleLikeRepository.findByMemberIdAndArticleId(memberId, postId);
        return record.map(MemberArticleLike::toDTO)
            .orElse(
                MemberArticleLikeDTO.builder().id(null).memberId(null).postId(null).isLike(null).build());
    }

    public void deleteRecord(DeleteMemberArticleLikeRequestDTO req, Long memberId) {
        MemberArticleLike memberArticleLike = memberArticleLikeRepository.findByMemberIdAndArticleId(memberId, req.getPostId())
            .orElseThrow(MemberArticleLikeNotFoundException::new);
        memberArticleLikeRepository.delete(memberArticleLike);
    }

    private void checkDuplicateRecords(long memberId, long postId, boolean isLike) {
        Optional<MemberArticleLike> record = memberArticleLikeRepository.findByMemberIdAndArticleId(memberId, postId);
        if (record.isPresent()) {
            if (record.get().isLike() == isLike) {
                throw new DuplicateMemberArticleLikeException();
            } else {
                throw new AlreadyExistsMemberArticleLikeException();
            }
        }
    }

}
