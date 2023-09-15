package com.fasttime.domain.comment.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.fasttime.domain.comment.dto.CommentDTO;
import com.fasttime.domain.comment.dto.request.CreateCommentRequest;
import com.fasttime.domain.comment.dto.request.DeleteCommentRequest;
import com.fasttime.domain.comment.dto.request.UpdateCommentRequest;
import com.fasttime.domain.comment.entity.Comment;
import com.fasttime.domain.comment.exception.CommentNotFoundException;
import com.fasttime.domain.comment.repository.CommentRepository;
import com.fasttime.domain.member.entity.Member;
import com.fasttime.domain.member.exception.UserNotFoundException;
import com.fasttime.domain.member.service.MemberService;
import com.fasttime.domain.post.entity.Post;
import com.fasttime.domain.post.exception.PostNotFoundException;
import com.fasttime.domain.post.repository.PostRepository;
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
    private PostRepository postRepository;

    @Mock
    private MemberService memberService;

    @Nested
    @DisplayName("createComment()는 ")
    class Context_createComment {

        @Test
        @DisplayName("비익명으로 댓글을 등록할 수 있다.")
        void nonAnonymous_willSuccess() {
            // given
            CreateCommentRequest request = CreateCommentRequest.builder().postId(0L).memberId(0L)
                .content("test").anonymity(false).parentCommentId(null).build();
            Optional<Post> post = Optional.of(Post.builder().id(0L).build());
            Member member = Member.builder().id(0L).build();
            Comment comment = Comment.builder().id(0L).post(post.get()).member(member)
                .content("test").anonymity(false).parentComment(null).build();

            given(postRepository.findById(any(Long.class))).willReturn(post);
            given(memberService.getMember(any(Long.class))).willReturn(member);
            given(commentRepository.save(any(Comment.class))).willReturn(comment);

            // when
            CommentDTO CommentDto = commentService.createComment(request);

            // then
            assertThat(CommentDto).extracting("id", "postId", "memberId", "content", "anonymity",
                "parentCommentId").containsExactly(0L, 0L, 0L, "test", false, null);

            verify(postRepository, times(1)).findById(any(Long.class));
            verify(memberService, times(1)).getMember(any(Long.class));
            verify(commentRepository, never()).findById(any(Long.class));
            verify(commentRepository, times(1)).save(any(Comment.class));
        }

        @Test
        @DisplayName("익명으로 댓글을 등록할 수 있다.")
        void anonymousComment_willSuccess() {
            // given
            CreateCommentRequest request = CreateCommentRequest.builder().postId(0L).memberId(0L)
                .content("test").anonymity(true).parentCommentId(null).build();
            Optional<Post> post = Optional.of(Post.builder().id(0L).build());
            Member member = Member.builder().id(0L).build();
            Comment comment = Comment.builder().id(0L).post(post.get()).member(member)
                .content("test").anonymity(true).parentComment(null).build();

            given(postRepository.findById(any(Long.class))).willReturn(post);
            given(memberService.getMember(any(Long.class))).willReturn(member);
            given(commentRepository.save(any(Comment.class))).willReturn(comment);

            // when
            CommentDTO CommentDto = commentService.createComment(request);

            // then
            assertThat(CommentDto).extracting("id", "postId", "memberId", "content", "anonymity",
                "parentCommentId").containsExactly(0L, 0L, 0L, "test", true, null);

            verify(postRepository, times(1)).findById(any(Long.class));
            verify(memberService, times(1)).getMember(any(Long.class));
            verify(commentRepository, never()).findById(any(Long.class));
            verify(commentRepository, times(1)).save(any(Comment.class));
        }

        @Test
        @DisplayName("비익명으로 대댓글을 등록할 수 있다.")
        void nonAnonymousReply_willSuccess() {
            // given
            CreateCommentRequest request = CreateCommentRequest.builder().postId(0L).memberId(0L)
                .content("test").anonymity(false).parentCommentId(0L).build();
            Optional<Post> post = Optional.of(Post.builder().id(0L).build());
            Member member = Member.builder().id(0L).build();
            Optional<Comment> parentComment = Optional.of(
                Comment.builder().id(0L).post(post.get()).member(member).content("test")
                    .anonymity(false).parentComment(null).build());
            Comment comment = Comment.builder().id(1L).post(post.get()).member(member)
                .content("test").anonymity(false).parentComment(parentComment.get()).build();

            given(commentRepository.findById(any(Long.class))).willReturn(parentComment);
            given(postRepository.findById(any(Long.class))).willReturn(post);
            given(memberService.getMember(any(Long.class))).willReturn(member);
            given(commentRepository.save(any(Comment.class))).willReturn(comment);
            // when
            CommentDTO CommentDto = commentService.createComment(request);

            // then
            assertThat(CommentDto).extracting("id", "postId", "memberId", "content", "anonymity",
                "parentCommentId").containsExactly(1L, 0L, 0L, "test", false, 0L);

            verify(postRepository, times(1)).findById(any(Long.class));
            verify(memberService, times(1)).getMember(any(Long.class));
            verify(commentRepository, times(1)).findById(any(Long.class));
            verify(commentRepository, times(1)).save(any(Comment.class));
        }

        @Test
        @DisplayName("익명으로 대댓글을 등록할 수 있다.")
        void anonymousReply_willSuccess() {
            // given
            CreateCommentRequest request = CreateCommentRequest.builder().postId(0L).memberId(0L)
                .content("test").anonymity(true).parentCommentId(0L).build();
            Optional<Post> post = Optional.of(Post.builder().id(0L).build());
            Member member = Member.builder().id(0L).build();
            Optional<Comment> parentComment = Optional.of(
                Comment.builder().id(0L).post(post.get()).member(member).content("test")
                    .anonymity(false).parentComment(null).build());
            Comment comment = Comment.builder().id(1L).post(post.get()).member(member)
                .content("test").anonymity(true).parentComment(parentComment.get()).build();

            given(commentRepository.findById(any(Long.class))).willReturn(parentComment);
            given(postRepository.findById(any(Long.class))).willReturn(post);
            given(memberService.getMember(any(Long.class))).willReturn(member);
            given(commentRepository.save(any(Comment.class))).willReturn(comment);

            // when
            CommentDTO CommentDto = commentService.createComment(request);

            // then
            assertThat(CommentDto).extracting("id", "postId", "memberId", "content", "anonymity",
                "parentCommentId").containsExactly(1L, 0L, 0L, "test", true, 0L);

            verify(postRepository, times(1)).findById(any(Long.class));
            verify(memberService, times(1)).getMember(any(Long.class));
            verify(commentRepository, times(1)).findById(any(Long.class));
            verify(commentRepository, times(1)).save(any(Comment.class));
        }

        @Test
        @DisplayName("게시물을 찾을 수 없으면 댓글을 등록할 수 없다.")
        void postNotFound_willFail() {
            // given
            CreateCommentRequest request = CreateCommentRequest.builder().postId(0L).memberId(0L)
                .content("test").anonymity(false).parentCommentId(null).build();
            Optional<Post> post = Optional.empty();

            given(postRepository.findById(any(Long.class))).willReturn(post);

            // when, then
            Throwable exception = assertThrows(PostNotFoundException.class, () -> {
                commentService.createComment(request);
            });
            assertEquals("존재하지 않는 게시글입니다.", exception.getMessage());

            verify(commentRepository, never()).findById(any(Long.class));
            verify(commentRepository, never()).save(any(Comment.class));
        }

        @Test
        @DisplayName("회원을 찾을 수 없으면 댓글을 등록할 수 없다.")
        void memberNotFound_willFail() {
            // given
            CreateCommentRequest request = CreateCommentRequest.builder().postId(0L).memberId(0L)
                .content("test").anonymity(false).parentCommentId(null).build();
            Optional<Post> post = Optional.of(Post.builder().id(0L).build());
            given(postRepository.findById(any(Long.class))).willReturn(post);
            given(memberService.getMember(any(Long.class))).willThrow(
                new UserNotFoundException("User not found with id: 0L"));

            // when, then
            Throwable exception = assertThrows(UserNotFoundException.class, () -> {
                commentService.createComment(request);
            });
            assertEquals("User not found with id: 0L", exception.getMessage());

            verify(postRepository, times(1)).findById(any(Long.class));
            verify(memberService, times(1)).getMember(any(Long.class));
            verify(commentRepository, never()).findById(any(Long.class));
            verify(commentRepository, never()).save(any(Comment.class));
        }

        @Test
        @DisplayName("댓글을 찾을 수 없으면 대댓글을 등록할 수 없다.")
        void parentCommentNotFound_willFail() {
            // given
            CreateCommentRequest request = CreateCommentRequest.builder().postId(0L).memberId(0L)
                .content("test").anonymity(false).parentCommentId(0L).build();

            given(commentRepository.findById(any(Long.class))).willReturn(Optional.empty());

            // when, then
            Throwable exception = assertThrows(CommentNotFoundException.class, () -> {
                commentService.createComment(request);
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
            UpdateCommentRequest request = UpdateCommentRequest.builder().id(0L).content("modified")
                .build();
            Post post = Post.builder().id(0L).build();
            Member member = Member.builder().id(0L).build();
            Optional<Comment> comment = Optional.of(
                Comment.builder().id(0L).post(post).member(member).content("test").anonymity(false)
                    .parentComment(null).build());
            given(commentRepository.findById(any(Long.class))).willReturn(comment);

            // when
            CommentDTO CommentDto = commentService.updateComment(request);

            // then
            assertThat(CommentDto).extracting("id", "postId", "memberId", "content", "anonymity",
                "parentCommentId").containsExactly(0L, 0L, 0L, "modified", false, null);

            verify(commentRepository, times(1)).findById(any(Long.class));
        }

        @Test
        @DisplayName("댓글을 찾을 수 없으면 댓글을 가져올 수 없다.")
        void CommentNotFound_willFail() {
            // given
            UpdateCommentRequest request = UpdateCommentRequest.builder().id(0L).content("modified")
                .build();
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
            DeleteCommentRequest request = DeleteCommentRequest.builder().id(0L).build();
            Post post = Post.builder().id(0L).build();
            Member member = Member.builder().id(0L).build();
            Optional<Comment> comment = Optional.of(
                Comment.builder().id(0L).post(post).member(member).content("test").anonymity(false)
                    .parentComment(null).build());

            given(commentRepository.findById(any(Long.class))).willReturn(comment);

            // when
            CommentDTO CommentDto = commentService.deleteComment(request);

            // then
            assertThat(CommentDto).extracting("id", "postId", "memberId", "content", "anonymity",
                "parentCommentId").containsExactly(0L, 0L, 0L, "test", false, null);

            verify(commentRepository, times(1)).findById(any(Long.class));
        }

        @Test
        @DisplayName("댓글을 찾을 수 없으면 댓글을 삭제할 수 없다.")
        void CommentNotFound_willFail() {
            // given
            DeleteCommentRequest request = DeleteCommentRequest.builder().id(0L).build();
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
            Post post = Post.builder().id(0L).build();
            Member member = Member.builder().id(0L).build();
            Optional<Comment> comment = Optional.of(
                Comment.builder().id(0L).post(post).member(member).content("test").anonymity(false)
                    .parentComment(null).build());

            given(commentRepository.findById(any(Long.class))).willReturn(comment);

            // when
            Comment result = commentService.getComment(0L);

            // then
            assertThat(result).extracting("id", "post", "member", "content", "anonymity",
                "parentComment").containsExactly(0L, post, member, "test", false, null);

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
                commentService.getComment(0L);
            });
            assertEquals("존재하지 않는 댓글입니다.", exception.getMessage());

            verify(commentRepository, times(1)).findById(any(Long.class));

        }
    }

    @Nested
    @DisplayName("getCommentsByPost()는 ")
    class Context_getCommentsByPost {

        @Test
        @DisplayName("해당 게시글의 댓글들을 불러올 수 있다.")
        void _willSuccess() {
            // given
            Post post = Post.builder().id(0L).build();
            Member member = Member.builder().id(0L).build();
            Comment comment = Comment.builder().id(0L).post(post).member(member).content("test")
                .anonymity(false).parentComment(null).build();
            List<Comment> commentList = new ArrayList<>();
            commentList.add(comment);
            Optional<List<Comment>> comments = Optional.of(commentList);
            List<CommentDTO> commentDTOList = new ArrayList<>();
            commentDTOList.add(comment.toDTO());

            given(commentRepository.findAllByPost(any(Post.class))).willReturn(comments);

            // when
            List<CommentDTO> result = commentService.getCommentsByPost(post);

            // then
            assertThat(result.get(0).getId()).isEqualTo(commentDTOList.get(0).getId());
            assertThat(result.get(0).getContent().equals(commentDTOList.get(0).getContent())).isTrue();

            verify(commentRepository, times(1)).findAllByPost(any(Post.class));
        }

        @Test
        @DisplayName("댓글을 찾을 수 없으면 빈 리스트를 반환한다.")
        void CommentNotFound_willFail() {
            // given
            Post post = Post.builder().id(0L).build();
            Optional<List<Comment>> comments = Optional.empty();

            given(commentRepository.findAllByPost(any(Post.class))).willReturn(comments);

            // when
            List<CommentDTO> result = commentService.getCommentsByPost(post);

            // then
            assertThat(result).isEmpty();

            verify(commentRepository, times(1)).findAllByPost(any(Post.class));
        }
    }

    @Nested
    @DisplayName("getCommentsByMember()는 ")
    class Context_getCommentsByMember {

        @Test
        @DisplayName("해당 회원의 댓글들을 불러올 수 있다.")
        void _willSuccess() {
            // given
            Post post = Post.builder().id(0L).build();
            Member member = Member.builder().id(0L).build();
            Comment comment = Comment.builder().id(0L).post(post).member(member).content("test")
                .anonymity(false).parentComment(null).build();

            List<Comment> commentList = new ArrayList<>();
            commentList.add(comment);
            Optional<List<Comment>> comments = Optional.of(commentList);

            List<CommentDTO> commentDTOList = new ArrayList<>();
            commentDTOList.add(comment.toDTO());

            given(commentRepository.findAllByMember(any(Member.class))).willReturn(comments);


            // when
            List<CommentDTO> result = commentService.getCommentsByMember(member);

            // then
            assertThat(result.get(0).getId()).isEqualTo(commentDTOList.get(0).getId());
            assertThat(result.get(0).getContent().equals(commentDTOList.get(0).getContent())).isTrue();

            verify(commentRepository, times(1)).findAllByMember(any(Member.class));
        }

        @Test
        @DisplayName("댓글을 찾을 수 없으면 빈 리스트를 반환한다.")
        void CommentNotFound_willFail() {
            // given
            Member member = Member.builder().id(0L).build();
            Optional<List<Comment>> comments = Optional.empty();

            given(commentRepository.findAllByMember(any(Member.class))).willReturn(comments);

            // when
            List<CommentDTO> result = commentService.getCommentsByMember(member);

            // then
            assertThat(result).isEmpty();

            verify(commentRepository, times(1)).findAllByMember(any(Member.class));
        }
    }
}
