package com.fasttime.domain.comment.service;

import com.fasttime.domain.comment.dto.CommentDTO;
import com.fasttime.domain.comment.dto.request.CreateCommentRequest;
import com.fasttime.domain.comment.dto.request.DeleteCommentRequest;
import com.fasttime.domain.comment.dto.request.UpdateCommentRequest;
import com.fasttime.domain.comment.entity.Comment;
import com.fasttime.domain.comment.exception.CommentNotFoundException;
import com.fasttime.domain.comment.repository.CommentRepository;
import com.fasttime.domain.member.entity.Member;
import com.fasttime.domain.member.exception.UserNotFoundException;
import com.fasttime.domain.member.repository.MemberRepository;
import com.fasttime.domain.post.entity.Post;
import com.fasttime.domain.post.exception.PostNotFoundException;
import com.fasttime.domain.post.repository.PostRepository;
import java.util.Optional;
import javax.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final MemberRepository memberRepository;

    public CommentDTO createComment(CreateCommentRequest req) {
        // TODO : post, member 각각 postService, memberService 를 통해 읽어 온다.
        Optional<Post> post = postRepository.findById(req.getPostId());
        Optional<Member> member = memberRepository.findById(req.getMemberId());
        Comment parentComment = null;
        // 대댓글인 경우
        if (req.getParentCommentId() != null) {
            Optional<Comment> Comment = commentRepository.findById(req.getParentCommentId());
            if (Comment.isEmpty()) {
                throw new CommentNotFoundException();
            } else {
                parentComment = Comment.get();
            }
        }
        if (post.isEmpty()) {
            throw new PostNotFoundException();
        } else if (member.isEmpty()) {
            throw new UserNotFoundException("존재하지 않는 회원입니다.");
        } else {
            return commentRepository.save(
                Comment.builder().post(post.get()).member(member.get()).content(req.getContent())
                    .anonymity(req.getAnonymity()).parentComment(parentComment).build()).toDTO();
        }
    }

    public CommentDTO getComment(Long id) {
        Optional<Comment> comment = commentRepository.findById(id);
        if (comment.isEmpty()) {
            throw new CommentNotFoundException();
        } else {
            return comment.get().toDTO();
        }
    }

    public CommentDTO deleteComment(DeleteCommentRequest req) {
        Optional<Comment> comment = commentRepository.findById(req.getId());
        if (comment.isEmpty()) {
            throw new CommentNotFoundException();
        } else {
            comment.get().deleteComment();
            return comment.get().toDTO();
        }
    }

    public CommentDTO updateComment(UpdateCommentRequest req) {
        Optional<Comment> comment = commentRepository.findById(req.getId());
        if (comment.isEmpty()) {
            throw new CommentNotFoundException();
        } else {
            comment.get().updateContent(req.getContent());
        }
        return comment.get().toDTO();
    }
}
