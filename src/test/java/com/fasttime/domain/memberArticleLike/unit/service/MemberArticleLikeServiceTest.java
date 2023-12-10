package com.fasttime.domain.memberArticleLike.unit.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

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
import com.fasttime.domain.memberArticleLike.service.MemberArticleLikeService;
import jakarta.transaction.Transactional;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@Transactional
@ExtendWith(MockitoExtension.class)
public class MemberArticleLikeServiceTest {

    @InjectMocks
    private MemberArticleLikeService memberArticleLikeService;

    @Mock
    private MemberArticleLikeRepository memberArticleLikeRepository;

    @Mock
    private ArticleQueryService postQueryService;

    @Mock
    private ArticleCommandService postCommandService;

    @Mock
    private MemberService memberService;

    private Member testMember;
    private ArticleResponse testArticle;
    private CreateMemberArticleLikeRequestDTO createLikeRequest;
    private CreateMemberArticleLikeRequestDTO createHateRequest;

    @BeforeEach
    public void setup() {
        testMember = Member.builder().id(1L).build();
        testArticle = ArticleResponse.builder().id(1L).build();
        createLikeRequest = CreateMemberArticleLikeRequestDTO.builder().postId(1L).isLike(true).build();
        createHateRequest = CreateMemberArticleLikeRequestDTO.builder().postId(1L).isLike(false).build();
    }

    private void mockForSuccess() {
        given(postQueryService.queryById(any(Long.class))).willReturn(testArticle);
        given(memberService.getMember(any(Long.class))).willReturn(testMember);
        given(memberArticleLikeRepository.findByMemberIdAndArticleId(any(long.class),any(long.class))).willReturn(Optional.empty());
    }


    @Nested
    @DisplayName("createMemberArticleLike()는 ")
    class Context_createMemberArticleLike {

        @Test
        @DisplayName("게시글을 좋아요 할 수 있다.")
        void like_willSuccess() {

            // given
            mockForSuccess();

            // when
            memberArticleLikeService.createMemberArticleLike(createLikeRequest, 1L);

            // then
            verify(memberArticleLikeRepository, times(1)).save(any(MemberArticleLike.class));
            verify(postCommandService, times(1)).likeOrHate(any(ArticleLikeOrHateServiceRequest.class));
        }

        @Test
        @DisplayName("게시글을 싫어요 할 수 있다.")
        void hate_willSuccess() {
            // given
            mockForSuccess();

            // when
            memberArticleLikeService.createMemberArticleLike(createHateRequest, 1L);

            // then
            verify(memberArticleLikeRepository, times(1)).save(any(MemberArticleLike.class));
            verify(postCommandService, times(1)).likeOrHate(any(ArticleLikeOrHateServiceRequest.class));
        }

        @Test
        @DisplayName("이미 좋아요(싫어요)를 한 게시글에 다시 좋아요(싫어요)를 등록할 수 없다.")
        void duplicateRecord1_willFail() {
            // given
            mockForSuccess();
            Optional<MemberArticleLike> record = Optional.of(
                MemberArticleLike.builder().member(testMember).article(Article.builder().id(testArticle.id()).build())
                    .isLike(true).build());

            given(memberArticleLikeRepository.findByMemberIdAndArticleId(any(long.class),
                any(long.class))).willReturn(record);

            // when, then
            Throwable exception = assertThrows(DuplicateMemberArticleLikeException.class, () -> memberArticleLikeService.createMemberArticleLike(createLikeRequest, 1L));
            assertEquals("중복된 좋아요/싫어요 등록 요청입니다.", exception.getMessage());
        }

        @Test
        @DisplayName("좋아요와 싫어요를 중복으로 등록할 수 없다.")
        void duplicateRecord2_willFail() {
            // given
            mockForSuccess();
            Optional<MemberArticleLike> record = Optional.of(
                MemberArticleLike.builder().member(testMember).article(Article.builder().id(testArticle.id()).build())
                    .isLike(false).build());

            given(memberArticleLikeRepository.findByMemberIdAndArticleId(any(long.class),
                any(long.class))).willReturn(record);

            // when, then
            Throwable exception = assertThrows(AlreadyExistsMemberArticleLikeException.class, () -> memberArticleLikeService.createMemberArticleLike(createLikeRequest, 1L));
            assertEquals("한 게시글에 좋아요와 싫어요를 모두 등록할 수는 없습니다.", exception.getMessage());
        }
    }

    @Nested
    @DisplayName("getMemberArticleLike()는 ")
    class Context_getMemberArticleLike {

        private Member member;
        private Article post;

        @BeforeEach
        void setUp() {
            member = Member.builder().id(1L).build();
            post = Article.builder().id(1L).build();
        }

        private void mockRepositoryResponse(Optional<MemberArticleLike> record) {
            given(memberArticleLikeRepository.findByMemberIdAndArticleId(any(Long.class), any(Long.class)))
                .willReturn(record);
        }

        private void assertMemberArticleLikeDTO(MemberArticleLikeDTO result, Long id, Long postId, Long memberId, Boolean isLike) {
            assertThat(result).extracting("id", "postId", "memberId", "isLike")
                .containsExactly(id, postId, memberId, isLike);
        }

        @Test
        @DisplayName("회원이 해당 게시물에 대해 등록한 좋아요/싫어요 내역을 가져올 수 있다.")
        void _willSuccess() {
            // given
            Optional<MemberArticleLike> record = Optional.of(MemberArticleLike.builder().id(1L).article(post).member(member).isLike(true).build());
            mockRepositoryResponse(record);

            // when
            MemberArticleLikeDTO result = memberArticleLikeService.getMemberArticleLike(1L, 1L);

            // then
            assertMemberArticleLikeDTO(result, 1L, 1L, 1L, true);
            verify(memberArticleLikeRepository, times(1)).findByMemberIdAndArticleId(any(Long.class), any(Long.class));
        }

        @Test
        @DisplayName("회원이 해당 게시물에 대해 등록한 좋아요/싫어요 데이터가 없다면 id값이 null인 ResponseDTO를 반환한다.")
        void noRecord_willSuccess() {
            // given
            mockRepositoryResponse(Optional.empty());

            // when
            MemberArticleLikeDTO result = memberArticleLikeService.getMemberArticleLike(1L, 1L);

            // then
            assertMemberArticleLikeDTO(result, null, null, null, null);
            verify(memberArticleLikeRepository, times(1)).findByMemberIdAndArticleId(any(Long.class), any(Long.class));
        }
    }


    @Nested
    @DisplayName("deleteMemberArticleLike()는 ")
    class Context_deleteMemberArticleLike {

        private Member member;
        private Article post;

        @BeforeEach
        void setUp() {
            member = Member.builder().id(1L).build();
            post = Article.builder().id(1L).build();
        }

        private void mockRepositoryResponse(Optional<MemberArticleLike> record) {
            given(memberArticleLikeRepository.findByMemberIdAndArticleId(1L, 1L)).willReturn(record);
        }

        @Test
        @DisplayName("게시글 좋아요/싫어요를 취소할 수 있다.")
        void _willSuccess() {
            // given
            DeleteMemberArticleLikeRequestDTO request = DeleteMemberArticleLikeRequestDTO.builder().postId(1L).build();
            Optional<MemberArticleLike> record = Optional.of(MemberArticleLike.builder().id(1L).member(member).article(post).isLike(true).build());
            mockRepositoryResponse(record);

            // when
            memberArticleLikeService.deleteMemberArticleLike(request, 1L);

            // then
            verify(memberArticleLikeRepository, times(1)).delete(any(MemberArticleLike.class));
        }

        @Test
        @DisplayName("게시글 좋아요/싫어요 한 적이 없다면 좋아요/싫어요를 취소할 수 없다.")
        void recordNotFound_willSuccess() {
            // given
            DeleteMemberArticleLikeRequestDTO request = DeleteMemberArticleLikeRequestDTO.builder().postId(1L).build();
            mockRepositoryResponse(Optional.empty());

            // when, then
            assertThrows(MemberArticleLikeNotFoundException.class, () -> memberArticleLikeService.deleteMemberArticleLike(request, 1L));
        }
    }

}
