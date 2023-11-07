package com.fasttime.domain.comment.unit.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.fasttime.domain.article.dto.service.response.ArticleResponse;
import com.fasttime.domain.article.entity.Article;
import com.fasttime.domain.article.entity.ReportStatus;
import com.fasttime.domain.article.exception.ArticleNotFoundException;
import com.fasttime.domain.article.service.ArticleQueryService;
import com.fasttime.domain.comment.dto.request.CreateCommentRequestDTO;
import com.fasttime.domain.comment.dto.request.GetCommentsRequestDTO;
import com.fasttime.domain.comment.dto.request.UpdateCommentRequestDTO;
import com.fasttime.domain.comment.dto.response.CommentResponseDTO;
import com.fasttime.domain.comment.entity.Comment;
import com.fasttime.domain.comment.exception.CommentNotFoundException;
import com.fasttime.domain.comment.exception.NotCommentAuthorException;
import com.fasttime.domain.comment.repository.CommentRepository;
import com.fasttime.domain.comment.service.CommentService;
import com.fasttime.domain.member.entity.Member;
import com.fasttime.domain.member.exception.UserNotFoundException;
import com.fasttime.domain.member.service.MemberService;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.transaction.Transactional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@Transactional
@ExtendWith(MockitoExtension.class)
public class CommentServiceTest {

    @InjectMocks
    private CommentService commentService;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private ArticleQueryService articleQueryService;

    @Mock
    private MemberService memberService;

    private ArticleResponse newArticleResponse() {
        return ArticleResponse.builder()
            .id(1L)
            .title("title")
            .content("content")
            .nickname("nickname1")
            .anonymity(true)
            .likeCount(0)
            .hateCount(0)
            .createdAt(LocalDateTime.of(2024, 1, 1, 12, 0, 0))
            .lastModifiedAt(LocalDateTime.of(2024, 1, 1, 12, 0, 0))
            .build();
    }

    private Article newArticle() {
        return Article.builder()
            .id(1L)
            .title("title")
            .content("content")
            .anonymity(true)
            .likeCount(0)
            .hateCount(0)
            .reportStatus(ReportStatus.NORMAL)
            .build();
    }

    private Member newMember() {
        return Member.builder()
            .id(1L)
            .email("email")
            .password("password")
            .nickname("nickname")
            .image("imageUrl")
            .build();
    }

    @Nested
    @DisplayName("createComment()는 ")
    class Context_createComment {

        @Test
        @DisplayName("비익명으로 댓글을 등록할 수 있다.")
        void nonAnonymous_willSuccess() {
            // given
            CreateCommentRequestDTO createCommentRequestDTO = CreateCommentRequestDTO.builder()
                .content("content")
                .anonymity(false)
                .parentCommentId(null)
                .build();
            given(articleQueryService.queryById(any(Long.class))).willReturn(newArticleResponse());
            given(memberService.getMember(any(Long.class))).willReturn(newMember());
            given(commentRepository.save(any(Comment.class))).willReturn(
                Comment.builder()
                    .id(1L)
                    .article(newArticle())
                    .member(newMember())
                    .content("content")
                    .anonymity(false)
                    .parentComment(null)
                    .build());

            // when
            CommentResponseDTO commentResponseDTO = commentService.createComment(1L, 1L,
                createCommentRequestDTO);

            // then
            assertThat(commentResponseDTO).extracting("commentId", "articleId", "memberId",
                    "nickname", "content", "anonymity", "parentCommentId", "childCommentCount")
                .containsExactly(1L, 1L, 1L, "nickname", "content", false, -1L, 0);
            verify(commentRepository, never()).findById(any(Long.class));
            verify(articleQueryService, times(1)).queryById(any(Long.class));
            verify(memberService, times(1)).getMember(any(Long.class));
            verify(commentRepository, times(1)).save(any(Comment.class));
        }

        @Test
        @DisplayName("익명으로 댓글을 등록할 수 있다.")
        void anonymousComment_willSuccess() {
            // given
            CreateCommentRequestDTO createCommentRequestDTO = CreateCommentRequestDTO.builder().
                content("content")
                .anonymity(true)
                .parentCommentId(null)
                .build();
            given(articleQueryService.queryById(any(Long.class))).willReturn(newArticleResponse());
            given(memberService.getMember(any(Long.class))).willReturn(newMember());
            given(commentRepository.save(any(Comment.class))).willReturn(
                Comment.builder()
                    .id(1L)
                    .article(newArticle())
                    .member(newMember())
                    .content("content")
                    .anonymity(true)
                    .parentComment(null)
                    .build());

            // when
            CommentResponseDTO commentResponseDTO = commentService.createComment(1L, 1L,
                createCommentRequestDTO);

            // then
            assertThat(commentResponseDTO).extracting("commentId", "articleId", "memberId",
                    "nickname", "content", "anonymity", "parentCommentId", "childCommentCount")
                .containsExactly(1L, 1L, 1L, "nickname", "content", true, -1L, 0);
            verify(commentRepository, never()).findById(any(Long.class));
            verify(articleQueryService, times(1)).queryById(any(Long.class));
            verify(memberService, times(1)).getMember(any(Long.class));
            verify(commentRepository, times(1)).save(any(Comment.class));
        }

        @Test
        @DisplayName("비익명으로 대댓글을 등록할 수 있다.")
        void nonAnonymousChildComment_willSuccess() {
            // given
            CreateCommentRequestDTO createCommentRequestDTO = CreateCommentRequestDTO.builder()
                .content("content2")
                .anonymity(false)
                .parentCommentId(1L)
                .build();
            Comment parentComment = Comment.builder()
                .id(1L)
                .article(newArticle())
                .member(newMember())
                .content("content1")
                .anonymity(false)
                .parentComment(null)
                .build();
            given(commentRepository.findById(any(Long.class))).willReturn(
                Optional.of(parentComment));
            given(articleQueryService.queryById(any(Long.class))).willReturn(
                ArticleResponse.builder().id(1L).build());
            given(memberService.getMember(any(Long.class))).willReturn(newMember());
            given(commentRepository.save(any(Comment.class))).willReturn(
                Comment.builder()
                    .id(2L)
                    .article(newArticle())
                    .member(newMember())
                    .content("content2")
                    .anonymity(false)
                    .parentComment(parentComment)
                    .build());

            // when
            CommentResponseDTO commentResponseDTO = commentService.createComment(1L, 1L,
                createCommentRequestDTO);

            // then
            assertThat(commentResponseDTO).extracting("commentId", "articleId", "memberId",
                    "nickname", "content", "anonymity", "parentCommentId", "childCommentCount")
                .containsExactly(2L, 1L, 1L, "nickname", "content2", false, 1L, 0);
            verify(commentRepository, times(1)).findById(any(Long.class));
            verify(articleQueryService, times(1)).queryById(any(Long.class));
            verify(memberService, times(1)).getMember(any(Long.class));
            verify(commentRepository, times(1)).save(any(Comment.class));
        }

        @Test
        @DisplayName("익명으로 대댓글을 등록할 수 있다.")
        void anonymousChildComment_willSuccess() {
            // given
            CreateCommentRequestDTO createCommentRequestDTO = CreateCommentRequestDTO.builder()
                .content("content2")
                .anonymity(true)
                .parentCommentId(1L)
                .build();
            Comment parentComment = Comment.builder()
                .id(1L)
                .article(newArticle())
                .member(newMember())
                .content("content1")
                .anonymity(false)
                .parentComment(null)
                .build();
            given(commentRepository.findById(any(Long.class))).willReturn(
                Optional.of(parentComment));
            given(articleQueryService.queryById(any(Long.class))).willReturn(
                newArticleResponse());
            given(memberService.getMember(any(Long.class))).willReturn(newMember());
            given(commentRepository.save(any(Comment.class))).willReturn(
                Comment.builder()
                    .id(2L)
                    .article(newArticle())
                    .member(newMember())
                    .content("content2")
                    .anonymity(true)
                    .parentComment(parentComment)
                    .build());

            // when
            CommentResponseDTO commentResponseDTO = commentService.createComment(1L, 1L,
                createCommentRequestDTO);

            // then
            assertThat(commentResponseDTO).extracting("commentId", "articleId", "memberId",
                    "nickname", "content", "anonymity", "parentCommentId", "childCommentCount")
                .containsExactly(2L, 1L, 1L, "nickname", "content2", true, 1L, 0);
            verify(commentRepository, times(1)).findById(any(Long.class));
            verify(articleQueryService, times(1)).queryById(any(Long.class));
            verify(memberService, times(1)).getMember(any(Long.class));
            verify(commentRepository, times(1)).save(any(Comment.class));
        }

        @Test
        @DisplayName("게시물을 찾을 수 없으면 댓글을 등록할 수 없다.")
        void postNotFound_willFail() {
            // given
            CreateCommentRequestDTO createCommentRequestDTO = CreateCommentRequestDTO.builder()
                .content("content")
                .anonymity(false)
                .parentCommentId(null)
                .build();
            given(articleQueryService.queryById(any(Long.class))).willThrow(
                new ArticleNotFoundException());

            // when, then
            Throwable exception = assertThrows(ArticleNotFoundException.class, () -> {
                commentService.createComment(1L, 1L, createCommentRequestDTO);
            });
            assertEquals("존재하지 않는 게시글입니다.", exception.getMessage());
            verify(commentRepository, never()).findById(any(Long.class));
            verify(articleQueryService, times(1)).queryById(any(Long.class));
            verify(memberService, never()).getMember(any(Long.class));
            verify(commentRepository, never()).save(any(Comment.class));
        }

        @Test
        @DisplayName("회원을 찾을 수 없으면 댓글을 등록할 수 없다.")
        void memberNotFound_willFail() {
            // given
            CreateCommentRequestDTO createCommentRequestDTO = CreateCommentRequestDTO.builder()
                .content("content")
                .anonymity(false)
                .parentCommentId(null)
                .build();
            given(articleQueryService.queryById(any(Long.class))).willReturn(
                newArticleResponse());
            given(memberService.getMember(any(Long.class))).willThrow(
                new UserNotFoundException("User not found with id: 1L"));

            // when, then
            Throwable exception = assertThrows(UserNotFoundException.class, () -> {
                commentService.createComment(1L, 1L, createCommentRequestDTO);
            });
            assertEquals("User not found with id: 1L", exception.getMessage());
            verify(commentRepository, never()).findById(any(Long.class));
            verify(articleQueryService, times(1)).queryById(any(Long.class));
            verify(memberService, times(1)).getMember(any(Long.class));
            verify(commentRepository, never()).save(any(Comment.class));
        }

        @Test
        @DisplayName("댓글을 찾을 수 없으면 대댓글을 등록할 수 없다.")
        void parentCommentNotFound_willFail() {
            // given
            CreateCommentRequestDTO createCommentRequestDTO = CreateCommentRequestDTO.builder()
                .content("test")
                .anonymity(false)
                .parentCommentId(1L)
                .build();
            given(commentRepository.findById(any(Long.class))).willReturn(Optional.empty());

            // when, then
            Throwable exception = assertThrows(CommentNotFoundException.class, () -> {
                commentService.createComment(1L, 1L, createCommentRequestDTO);
            });
            assertEquals("존재하지 않는 댓글입니다.", exception.getMessage());
            verify(commentRepository, times(1)).findById(any(Long.class));
            verify(articleQueryService, never()).queryById(any(Long.class));
            verify(memberService, never()).getMember(any(Long.class));
            verify(commentRepository, never()).save(any(Comment.class));
        }
    }

    @Nested
    @DisplayName("getComments()는 ")
    class Context_getComments {

        @Test
        @DisplayName("해당 게시글의 댓글들을 불러올 수 있다.")
        void getCommentsByArticle_willSuccess() {
            // given
            GetCommentsRequestDTO getCommentsRequestDTO = GetCommentsRequestDTO.builder()
                .articleId(1L)
                .build();
            Comment comment1 = Comment.builder()
                .id(1L)
                .article(newArticle())
                .member(newMember())
                .content("content1")
                .anonymity(false)
                .parentComment(null)
                .build();
            Comment comment2 = Comment.builder()
                .id(2L)
                .article(newArticle())
                .member(newMember())
                .content("content2")
                .anonymity(false)
                .parentComment(comment1)
                .build();
            Comment comment3 = Comment.builder()
                .id(3L)
                .article(newArticle())
                .member(newMember())
                .content("content3")
                .anonymity(false)
                .parentComment(null)
                .build();
            comment1.getChildComments().add(comment2);
            given(commentRepository.findAllBySearchCondition(
                any(GetCommentsRequestDTO.class))).willReturn(List.of(comment1, comment3));

            // when
            List<CommentResponseDTO> result = commentService.getComments(getCommentsRequestDTO);

            // then
            assertThat(result).isNotEmpty();
            assertThat(result.get(0)).extracting("commentId", "articleId", "memberId", "nickname",
                    "content", "anonymity", "parentCommentId", "childCommentCount")
                .containsExactly(1L, 1L, 1L, "nickname", "content1", false, -1L, 1);
            assertThat(result.get(1)).extracting("commentId", "articleId", "memberId", "nickname",
                    "content", "anonymity", "parentCommentId", "childCommentCount")
                .containsExactly(3L, 1L, 1L, "nickname", "content3", false, -1L, 0);
            verify(commentRepository, times(1)).findAllBySearchCondition(
                any(GetCommentsRequestDTO.class));
        }

        @Test
        @DisplayName("해당 회원의 댓글들을 불러올 수 있다.")
        void getCommentsByMember_willSuccess() {
            // given
            GetCommentsRequestDTO getCommentsRequestDTO = GetCommentsRequestDTO.builder()
                .memberId(1L)
                .build();
            Comment comment1 = Comment.builder()
                .id(1L)
                .article(newArticle())
                .member(newMember())
                .content("content1")
                .anonymity(false)
                .parentComment(null).build();
            Comment comment2 = Comment.builder()
                .id(2L)
                .article(newArticle())
                .member(newMember())
                .content("content2")
                .anonymity(false)
                .parentComment(comment1)
                .build();
            Comment comment3 = Comment.builder()
                .id(3L)
                .article(newArticle())
                .member(newMember())
                .content("content3")
                .anonymity(false)
                .parentComment(null)
                .build();
            comment1.getChildComments().add(comment2);
            given(commentRepository.findAllBySearchCondition(
                any(GetCommentsRequestDTO.class))).willReturn(
                List.of(comment1, comment2, comment3));

            // when
            List<CommentResponseDTO> result = commentService.getComments(getCommentsRequestDTO);

            // then
            assertThat(result).isNotEmpty();
            assertThat(result.get(0)).extracting("commentId", "articleId", "memberId", "nickname",
                    "content", "anonymity", "parentCommentId", "childCommentCount")
                .containsExactly(1L, 1L, 1L, "nickname", "content1", false, -1L, 1);
            assertThat(result.get(1)).extracting("commentId", "articleId", "memberId", "nickname",
                    "content", "anonymity", "parentCommentId", "childCommentCount")
                .containsExactly(2L, 1L, 1L, "nickname", "content2", false, 1L, 0);
            assertThat(result.get(2)).extracting("commentId", "articleId", "memberId", "nickname",
                    "content", "anonymity", "parentCommentId", "childCommentCount")
                .containsExactly(3L, 1L, 1L, "nickname", "content3", false, -1L, 0);
            verify(commentRepository, times(1)).findAllBySearchCondition(
                any(GetCommentsRequestDTO.class));
        }

        @Test
        @DisplayName("해당 댓글의 대댓글들을 불러올 수 있다.")
        void getCommentsByParentComment_willSuccess() {
            // given
            GetCommentsRequestDTO getCommentsRequestDTO = GetCommentsRequestDTO.builder()
                .parentCommentId(1L)
                .build();
            Comment comment1 = Comment.builder()
                .id(1L)
                .article(newArticle())
                .member(newMember())
                .content("content1")
                .anonymity(false)
                .parentComment(null)
                .build();
            Comment comment2 = Comment.builder()
                .id(2L)
                .article(newArticle())
                .member(newMember())
                .content("content2")
                .anonymity(false)
                .parentComment(comment1)
                .build();
            comment1.getChildComments().add(comment2);
            given(commentRepository.findAllBySearchCondition(
                any(GetCommentsRequestDTO.class))).willReturn(List.of(comment2));

            // when
            List<CommentResponseDTO> result = commentService.getComments(getCommentsRequestDTO);

            // then
            assertThat(result).isNotEmpty();
            assertThat(result.get(0)).extracting("commentId", "articleId", "memberId", "nickname",
                    "content", "anonymity", "parentCommentId", "childCommentCount")
                .containsExactly(2L, 1L, 1L, "nickname", "content2", false, 1L, 0);
            verify(commentRepository, times(1)).findAllBySearchCondition(
                any(GetCommentsRequestDTO.class));
        }

        @Test
        @DisplayName("댓글을 찾을 수 없으면 빈 리스트를 반환한다.")
        void CommentNotFound_willFail() {
            // given
            given(commentRepository.findAllBySearchCondition(
                any(GetCommentsRequestDTO.class))).willReturn(new ArrayList<>());

            // when
            List<CommentResponseDTO> result = commentService.getComments(
                GetCommentsRequestDTO.builder().articleId(1L).build());

            // then
            assertThat(result).isEmpty();
            verify(commentRepository, times(1)).findAllBySearchCondition(
                any(GetCommentsRequestDTO.class));
        }
    }

    @Nested
    @DisplayName("updateComment()는 ")
    class Context_updateComment {

        @Test
        @DisplayName("댓글을 수정할 수 있다.")
        void _willSuccess() {
            // given
            UpdateCommentRequestDTO updateCommentRequestDTO = UpdateCommentRequestDTO.builder()
                .content("content2")
                .build();
            given(commentRepository.findById(any(Long.class))).willReturn(Optional.of(
                Comment.builder()
                    .id(1L)
                    .article(newArticle())
                    .member(newMember())
                    .content("content1")
                    .anonymity(false)
                    .parentComment(null)
                    .build()));

            // when
            CommentResponseDTO commentResponseDTO = commentService.updateComment(1L,
                1L, updateCommentRequestDTO);

            // then
            assertThat(commentResponseDTO).extracting("commentId", "articleId", "memberId",
                    "nickname", "content", "anonymity", "parentCommentId", "childCommentCount")
                .containsExactly(1L, 1L, 1L, "nickname", "content2", false, -1L, 0);
            verify(commentRepository, times(1)).findById(any(Long.class));
        }

        @Test
        @DisplayName("댓글을 찾을 수 없으면 댓글을 가져올 수 없다.")
        void CommentNotFound_willFail() {
            // given
            UpdateCommentRequestDTO updateCommentRequestDTO = UpdateCommentRequestDTO.builder()
                .content("content2")
                .build();
            given(commentRepository.findById(any(Long.class))).willReturn(Optional.empty());

            // when, then
            Throwable exception = assertThrows(CommentNotFoundException.class, () -> {
                commentService.updateComment(1L, 1L, updateCommentRequestDTO);
            });
            assertEquals("존재하지 않는 댓글입니다.", exception.getMessage());
            verify(commentRepository, times(1)).findById(any(Long.class));
        }

        @Test
        @DisplayName("댓글 작성자가 아니면 댓글을 수정할 수 없다.")
        void CommentUnauthorized_willFail() {
            // given
            UpdateCommentRequestDTO updateCommentRequestDTO = UpdateCommentRequestDTO.builder()
                .content("content2")
                .build();
            given(commentRepository.findById(any(Long.class))).willReturn(Optional.of(
                Comment.builder()
                    .id(1L)
                    .article(newArticle())
                    .member(newMember())
                    .content("content1")
                    .anonymity(false)
                    .parentComment(null)
                    .build()));

            // when, then
            Throwable exception = assertThrows(NotCommentAuthorException.class, () -> {
                commentService.updateComment(1L, 2L, updateCommentRequestDTO);
            });
            assertEquals("댓글 작성자만 해당 댓글 수정/삭제가 가능합니다.", exception.getMessage());
            verify(commentRepository, times(1)).findById(any(Long.class));
        }
    }

    @Nested
    @DisplayName("deleteComment()는 ")
    class Context_deleteComment {

        @Test
        @DisplayName("댓글을 삭제할 수 있다.")
        void _willSuccess() {
            // given
            given(commentRepository.findById(any(Long.class))).willReturn(Optional.of(
                Comment.builder()
                    .id(1L)
                    .article(newArticle())
                    .member(newMember())
                    .content("content")
                    .anonymity(false)
                    .parentComment(null)
                    .build()));

            // when
            CommentResponseDTO commentResponseDTO = commentService.deleteComment(1L, 1L);

            // then
            assertThat(commentResponseDTO).extracting("commentId", "articleId", "memberId",
                    "nickname", "content", "anonymity", "parentCommentId", "childCommentCount")
                .containsExactly(1L, 1L, 1L, "nickname", "content", false, -1L, 0);
            verify(commentRepository, times(1)).findById(any(Long.class));
        }

        @Test
        @DisplayName("댓글을 찾을 수 없으면 댓글을 삭제할 수 없다.")
        void CommentNotFound_willFail() {
            // given
            given(commentRepository.findById(any(Long.class))).willReturn(Optional.empty());

            // when, then
            Throwable exception = assertThrows(CommentNotFoundException.class, () -> {
                commentService.deleteComment(1L, 1L);
            });
            assertEquals("존재하지 않는 댓글입니다.", exception.getMessage());
            verify(commentRepository, times(1)).findById(any(Long.class));
        }

        @Test
        @DisplayName("댓글 작성자가 아니면 댓글을 삭제할 수 없다.")
        void CommentUnauthorized_willFail() {
            // given
            given(commentRepository.findById(any(Long.class))).willReturn(Optional.of(
                Comment.builder()
                    .id(1L)
                    .article(newArticle())
                    .member(newMember())
                    .content("content1")
                    .anonymity(false)
                    .parentComment(null)
                    .build()));

            // when, then
            Throwable exception = assertThrows(NotCommentAuthorException.class, () -> {
                commentService.deleteComment(1L, 2L);
            });
            assertEquals("댓글 작성자만 해당 댓글 수정/삭제가 가능합니다.", exception.getMessage());
            verify(commentRepository, times(1)).findById(any(Long.class));
        }
    }

    @Nested
    @DisplayName("getComment()는 ")
    class Context_getComment {

        @Test
        @DisplayName("댓글을 가져올 수 있다.")
        void _willSuccess() {
            // given
            Article article = newArticle();
            Member member = newMember();
            given(commentRepository.findById(any(Long.class))).willReturn(Optional.of(
                Comment.builder().id(1L).article(article).member(member).content("test")
                    .anonymity(false).parentComment(null).build()));

            // when
            Comment result = commentService.getComment(1L);

            // then
            assertThat(result).extracting("id", "article", "member", "content", "anonymity",
                    "parentComment", "childComments")
                .containsExactly(1L, article, member, "test", false, null,
                    new ArrayList<>());

            verify(commentRepository, times(1)).findById(any(Long.class));
        }

        @Test
        @DisplayName("댓글을 찾을 수 없으면 댓글을 가져올 수 없다.")
        void CommentNotFound_willFail() {
            // given
            given(commentRepository.findById(any(Long.class))).willReturn(Optional.empty());

            // when, then
            Throwable exception = assertThrows(CommentNotFoundException.class, () -> {
                commentService.getComment(1L);
            });
            assertEquals("존재하지 않는 댓글입니다.", exception.getMessage());
            verify(commentRepository, times(1)).findById(any(Long.class));

        }
    }
}
