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
import com.fasttime.domain.article.exception.ArticleNotFoundException;
import com.fasttime.domain.article.service.ArticleQueryService;
import com.fasttime.domain.comment.dto.request.GetCommentsRequestDTO;
import com.fasttime.domain.comment.dto.request.CreateCommentRequestDTO;
import com.fasttime.domain.comment.dto.request.DeleteCommentRequestDTO;
import com.fasttime.domain.comment.dto.request.UpdateCommentRequestDTO;
import com.fasttime.domain.comment.dto.response.CommentResponseDTO;
import com.fasttime.domain.comment.entity.Comment;
import com.fasttime.domain.comment.exception.CommentNotFoundException;
import com.fasttime.domain.comment.repository.CommentCustomRepository;
import com.fasttime.domain.comment.repository.CommentRepository;
import com.fasttime.domain.comment.service.CommentService;
import com.fasttime.domain.member.entity.Member;
import com.fasttime.domain.member.exception.UserNotFoundException;
import com.fasttime.domain.member.service.MemberService;
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
    private CommentCustomRepository commentCustomRepository;

    @Mock
    private ArticleQueryService postQueryService;

    @Mock
    private MemberService memberService;

    @Nested
    @DisplayName("createComment()는 ")
    class Context_createComment {

        @Test
        @DisplayName("비익명으로 댓글을 등록할 수 있다.")
        void nonAnonymous_willSuccess() {
            // given
            CreateCommentRequestDTO request = CreateCommentRequestDTO.builder().postId(1L)
                .content("test").anonymity(false).parentCommentId(null).build();
            Member member = Member.builder().id(1L).nickname("testNickname").build();
            Comment comment = Comment.builder().id(1L).article(Article.builder().id(1L).build())
                .member(member).content("test").anonymity(false).parentComment(null).build();
            given(postQueryService.queryById(any(Long.class))).willReturn(
                ArticleResponse.builder().id(0L).build());
            given(memberService.getMember(any(Long.class))).willReturn(member);
            given(commentRepository.save(any(Comment.class))).willReturn(comment);

            // when
            commentService.createComment(request, 1L);

            // then
            verify(postQueryService, times(1)).queryById(any(Long.class));
            verify(memberService, times(1)).getMember(any(Long.class));
            verify(commentRepository, never()).findById(any(Long.class));
            verify(commentRepository, times(1)).save(any(Comment.class));
        }

        @Test
        @DisplayName("익명으로 댓글을 등록할 수 있다.")
        void anonymousComment_willSuccess() {
            // given
            CreateCommentRequestDTO request = CreateCommentRequestDTO.builder().postId(1L)
                .content("test").anonymity(true).parentCommentId(null).build();
            Article post = Article.builder().id(1L).build();
            Member member = Member.builder().id(1L).nickname("testNickname").build();
            Comment comment = Comment.builder().id(1L).article(post).member(member).content("test")
                .anonymity(true).parentComment(null).build();
            given(postQueryService.queryById(any(Long.class))).willReturn(
                ArticleResponse.builder().id(1L).build());
            given(memberService.getMember(any(Long.class))).willReturn(member);
            given(commentRepository.save(any(Comment.class))).willReturn(comment);

            // when
            commentService.createComment(request, 1L);

            // then
            verify(postQueryService, times(1)).queryById(any(Long.class));
            verify(memberService, times(1)).getMember(any(Long.class));
            verify(commentRepository, never()).findById(any(Long.class));
            verify(commentRepository, times(1)).save(any(Comment.class));
        }

        @Test
        @DisplayName("비익명으로 대댓글을 등록할 수 있다.")
        void nonAnonymousReply_willSuccess() {
            // given
            CreateCommentRequestDTO request = CreateCommentRequestDTO.builder().postId(1L)
                .content("test").anonymity(false).parentCommentId(1L).build();
            Article post = Article.builder().id(1L).build();
            Member member = Member.builder().id(1L).nickname("testNickname").build();
            Optional<Comment> parentComment = Optional.of(
                Comment.builder().id(1L).article(post).member(member).content("test")
                    .anonymity(false).parentComment(null).build());
            Comment comment = Comment.builder().id(1L).article(post).member(member).content("test")
                .anonymity(false).parentComment(parentComment.get()).build();
            given(commentRepository.findById(any(Long.class))).willReturn(parentComment);
            given(postQueryService.queryById(any(Long.class))).willReturn(
                ArticleResponse.builder().id(1L).build());
            given(memberService.getMember(any(Long.class))).willReturn(member);
            given(commentRepository.save(any(Comment.class))).willReturn(comment);

            // when
            commentService.createComment(request, 1L);

            // then
            verify(postQueryService, times(1)).queryById(any(Long.class));
            verify(memberService, times(1)).getMember(any(Long.class));
            verify(commentRepository, times(1)).findById(any(Long.class));
            verify(commentRepository, times(1)).save(any(Comment.class));
        }

        @Test
        @DisplayName("익명으로 대댓글을 등록할 수 있다.")
        void anonymousReply_willSuccess() {
            // given
            CreateCommentRequestDTO request = CreateCommentRequestDTO.builder().postId(1L)
                .content("test").anonymity(true).parentCommentId(1L).build();
            Article post = Article.builder().id(1L).build();
            Member member = Member.builder().id(1L).nickname("testNickname").build();
            Optional<Comment> parentComment = Optional.of(
                Comment.builder().id(1L).article(post).member(member).content("test")
                    .anonymity(false).parentComment(null).build());
            Comment comment = Comment.builder().id(1L).article(post).member(member).content("test")
                .anonymity(true).parentComment(parentComment.get()).build();
            given(commentRepository.findById(any(Long.class))).willReturn(parentComment);
            given(postQueryService.queryById(any(Long.class))).willReturn(
                ArticleResponse.builder().id(1L).build());
            given(memberService.getMember(any(Long.class))).willReturn(member);
            given(commentRepository.save(any(Comment.class))).willReturn(comment);

            // when
            commentService.createComment(request, 1L);

            // then
            verify(postQueryService, times(1)).queryById(any(Long.class));
            verify(memberService, times(1)).getMember(any(Long.class));
            verify(commentRepository, times(1)).findById(any(Long.class));
            verify(commentRepository, times(1)).save(any(Comment.class));
        }

        @Test
        @DisplayName("게시물을 찾을 수 없으면 댓글을 등록할 수 없다.")
        void postNotFound_willFail() {
            // given
            CreateCommentRequestDTO request = CreateCommentRequestDTO.builder().postId(1L)
                .content("test").anonymity(false).parentCommentId(null).build();
            given(postQueryService.queryById(any(Long.class))).willThrow(
                new ArticleNotFoundException());

            // when, then
            Throwable exception = assertThrows(ArticleNotFoundException.class, () -> {
                commentService.createComment(request, 1L);
            });
            assertEquals("존재하지 않는 게시글입니다.", exception.getMessage());
            verify(commentRepository, never()).findById(any(Long.class));
            verify(commentRepository, never()).save(any(Comment.class));
        }

        @Test
        @DisplayName("회원을 찾을 수 없으면 댓글을 등록할 수 없다.")
        void memberNotFound_willFail() {
            // given
            CreateCommentRequestDTO request = CreateCommentRequestDTO.builder().postId(1L)
                .content("test").anonymity(false).parentCommentId(null).build();
            given(postQueryService.queryById(any(Long.class))).willReturn(
                ArticleResponse.builder().id(1L).build());
            given(memberService.getMember(any(Long.class))).willThrow(
                new UserNotFoundException("User not found with id: 1L"));

            // when, then
            Throwable exception = assertThrows(UserNotFoundException.class, () -> {
                commentService.createComment(request, 1L);
            });
            assertEquals("User not found with id: 1L", exception.getMessage());
            verify(postQueryService, times(1)).queryById(any(Long.class));
            verify(memberService, times(1)).getMember(any(Long.class));
            verify(commentRepository, never()).findById(any(Long.class));
            verify(commentRepository, never()).save(any(Comment.class));
        }

        @Test
        @DisplayName("댓글을 찾을 수 없으면 대댓글을 등록할 수 없다.")
        void parentCommentNotFound_willFail() {
            // given
            CreateCommentRequestDTO request = CreateCommentRequestDTO.builder().postId(1L)
                .content("test").anonymity(false).parentCommentId(1L).build();
            given(commentRepository.findById(any(Long.class))).willReturn(Optional.empty());

            // when, then
            Throwable exception = assertThrows(CommentNotFoundException.class, () -> {
                commentService.createComment(request, 1L);
            });
            assertEquals("존재하지 않는 댓글입니다.", exception.getMessage());
            verify(commentRepository, times(1)).findById(any(Long.class));
            verify(commentRepository, never()).save(any(Comment.class));
        }
    }

    @Nested
    @DisplayName("updateComment()는 ")
    class Context_updateComment {

        @Test
        @DisplayName("댓글을 수정할 수 있다.")
        void _willSuccess() {
            // given
            UpdateCommentRequestDTO request = UpdateCommentRequestDTO.builder().id(1L)
                .content("modified").build();
            Article post = Article.builder().id(1L).build();
            Member member = Member.builder().id(1L).nickname("testNickname").build();
            Optional<Comment> comment = Optional.of(
                Comment.builder().id(1L).article(post).member(member).content("test")
                    .anonymity(false).parentComment(null).build());
            given(commentRepository.findById(any(Long.class))).willReturn(comment);

            // when
            commentService.updateComment(request);

            // then
            verify(commentRepository, times(1)).findById(any(Long.class));
        }

        @Test
        @DisplayName("댓글을 찾을 수 없으면 댓글을 가져올 수 없다.")
        void CommentNotFound_willFail() {
            // given
            UpdateCommentRequestDTO request = UpdateCommentRequestDTO.builder().id(0L)
                .content("modified").build();
            Optional<Comment> comment = Optional.empty();
            given(commentRepository.findById(any(Long.class))).willReturn(comment);

            // when, then
            Throwable exception = assertThrows(CommentNotFoundException.class, () -> {
                commentService.updateComment(request);
            });
            assertEquals("존재하지 않는 댓글입니다.", exception.getMessage());
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
            DeleteCommentRequestDTO request = DeleteCommentRequestDTO.builder().id(0L).build();
            Article post = Article.builder().id(1L).build();
            Member member = Member.builder().id(1L).nickname("testNickname").build();
            Optional<Comment> comment = Optional.of(
                Comment.builder().id(1L).article(post).member(member).content("test")
                    .anonymity(false).parentComment(null).build());
            given(commentRepository.findById(any(Long.class))).willReturn(comment);

            // when
            commentService.deleteComment(request);

            // then
            verify(commentRepository, times(1)).findById(any(Long.class));
        }

        @Test
        @DisplayName("댓글을 찾을 수 없으면 댓글을 삭제할 수 없다.")
        void CommentNotFound_willFail() {
            // given
            DeleteCommentRequestDTO request = DeleteCommentRequestDTO.builder().id(1L).build();
            Optional<Comment> comment = Optional.empty();
            given(commentRepository.findById(any(Long.class))).willReturn(comment);

            // when, then
            Throwable exception = assertThrows(CommentNotFoundException.class, () -> {
                commentService.deleteComment(request);
            });
            assertEquals("존재하지 않는 댓글입니다.", exception.getMessage());
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
            Article post = Article.builder().id(1L).build();
            Member member = Member.builder().id(1L).build();
            Optional<Comment> comment = Optional.of(
                Comment.builder().id(1L).article(post).member(member).content("test")
                    .anonymity(false).parentComment(null).build());

            given(commentRepository.findById(any(Long.class))).willReturn(comment);

            // when
            Comment result = commentService.getComment(1L);

            // then
            assertThat(result).extracting("id", "article", "member", "content", "anonymity",
                "parentComment").containsExactly(1L, post, member, "test", false, null);

            verify(commentRepository, times(1)).findById(any(Long.class));
        }

        @Test
        @DisplayName("댓글을 찾을 수 없으면 댓글을 가져올 수 없다.")
        void CommentNotFound_willFail() {
            // given
            Optional<Comment> comment = Optional.empty();

            given(commentRepository.findById(any(Long.class))).willReturn(comment);

            // when, then
            Throwable exception = assertThrows(CommentNotFoundException.class, () -> {
                commentService.getComment(1L);
            });
            assertEquals("존재하지 않는 댓글입니다.", exception.getMessage());

            verify(commentRepository, times(1)).findById(any(Long.class));

        }
    }

    @Nested
    @DisplayName("getComments()는 ")
    class Context_getComments {

        @Test
        @DisplayName("해당 게시글의 댓글들을 불러올 수 있다.")
        void getCommentsByArticle_willSuccess() {
            // given
            Article article = Article.builder().id(1L).build();
            Member member = Member.builder().id(1L).build();
            Comment comment = Comment.builder().id(1L).article(article).member(member)
                .content("test").anonymity(false).parentComment(null).build();
            Comment child = Comment.builder().id(2L).article(article).member(member).content("test")
                .anonymity(false).parentComment(comment).build();
            comment.getChildComments().add(child);
            List<Comment> commentList = new ArrayList<>();
            commentList.add(comment);
            commentList.add(child);
            List<CommentResponseDTO> commentDTOList = new ArrayList<>();
            commentDTOList.add(comment.toCommentResponseDTO());
            given(commentCustomRepository.findAllBySearchCondition(
                any(GetCommentsRequestDTO.class))).willReturn(commentList);

            // when
            List<CommentResponseDTO> result = commentService.getComments(
                GetCommentsRequestDTO.builder().articleId(1L).build());

            // then
            assertThat(result.get(0).getCommentId()).isEqualTo(commentDTOList.get(0).getCommentId());
            assertThat(
                result.get(0).getContent().equals(commentDTOList.get(0).getContent())).isTrue();
            verify(commentCustomRepository, times(1)).findAllBySearchCondition(any(
                GetCommentsRequestDTO.class));
        }

        @Test
        @DisplayName("해당 회원의 댓글들을 불러올 수 있다.")
        void getCommentsByMember_willSuccess() {
            // given
            Article post = Article.builder().id(1L).build();
            Member member = Member.builder().id(1L).build();
            Comment comment = Comment.builder().id(1L).article(post).member(member).content("test")
                .anonymity(false).parentComment(null).build();
            List<Comment> commentList = new ArrayList<>();
            commentList.add(comment);
            List<CommentResponseDTO> commentDTOList = new ArrayList<>();
            commentDTOList.add(comment.toCommentResponseDTO());

            given(commentCustomRepository.findAllBySearchCondition(
                any(GetCommentsRequestDTO.class))).willReturn(commentList);

            // when
            List<CommentResponseDTO> result = commentService.getComments(
                GetCommentsRequestDTO.builder().memberId(1L).build());

            // then
            assertThat(result.get(0).getCommentId()).isEqualTo(commentDTOList.get(0).getCommentId());
            assertThat(
                result.get(0).getContent().equals(commentDTOList.get(0).getContent())).isTrue();
            verify(commentCustomRepository, times(1)).findAllBySearchCondition(
                any(GetCommentsRequestDTO.class));
        }

        @Test
        @DisplayName("해당 댓글의 대댓글들을 불러올 수 있다.")
        void getCommentsByParentComment_willSuccess() {
            // given
            Article post = Article.builder().id(1L).build();
            Member member = Member.builder().id(1L).build();
            Comment comment = Comment.builder().id(2L).article(post).member(member).content("test")
                .anonymity(false).parentComment(Comment.builder().id(1L).build()).build();
            List<Comment> commentList = new ArrayList<>();
            commentList.add(comment);
            List<CommentResponseDTO> commentDTOList = new ArrayList<>();
            commentDTOList.add(comment.toCommentResponseDTO());

            given(commentCustomRepository.findAllBySearchCondition(
                any(GetCommentsRequestDTO.class))).willReturn(commentList);

            // when
            List<CommentResponseDTO> result = commentService.getComments(
                GetCommentsRequestDTO.builder().parentCommentId(1L).build());

            // then
            assertThat(result.get(0).getCommentId()).isEqualTo(commentDTOList.get(0).getCommentId());
            assertThat(
                result.get(0).getContent().equals(commentDTOList.get(0).getContent())).isTrue();
            verify(commentCustomRepository, times(1)).findAllBySearchCondition(
                any(GetCommentsRequestDTO.class));
        }

        @Test
        @DisplayName("댓글을 찾을 수 없으면 빈 리스트를 반환한다.")
        void CommentNotFound_willFail() {
            // given
            Article post = Article.builder().id(1L).build();
            List<Comment> comments = new ArrayList<>();
            given(commentCustomRepository.findAllBySearchCondition(
                any(GetCommentsRequestDTO.class))).willReturn(comments);

            // when
            List<CommentResponseDTO> result = commentService.getComments(
                GetCommentsRequestDTO.builder().articleId(1L).build());

            // then
            assertThat(result).isEmpty();
            verify(commentCustomRepository, times(1)).findAllBySearchCondition(
                any(GetCommentsRequestDTO.class));
        }
    }
}
