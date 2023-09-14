package com.fasttime.domain.comment.service;

import com.fasttime.domain.comment.dto.CommentDTO;
import com.fasttime.domain.comment.dto.request.CreateCommentRequest;
import com.fasttime.domain.comment.dto.request.DeleteCommentRequest;
import com.fasttime.domain.comment.dto.request.UpdateCommentRequest;
import com.fasttime.domain.comment.entity.Comment;
import com.fasttime.domain.comment.exception.CommentNotFoundException;
import com.fasttime.domain.comment.repository.CommentRepository;
import com.fasttime.domain.member.entity.Member;
import com.fasttime.domain.member.service.MemberService;
import com.fasttime.domain.post.entity.Post;
import com.fasttime.domain.post.exception.PostNotFoundException;
import com.fasttime.domain.post.repository.PostRepository;
import java.util.ArrayList;
import java.util.List;
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
    private final MemberService memberService;

    /**
     * 댓글을 등록하는 메서드
     * @param req 등록할 댓글 정보가 담긴 객체
     * @return 등록한 댓글 DTO
     */
    public CommentDTO createComment(CreateCommentRequest req) {
        Comment parentComment = null;
        // 대댓글인 경우
        if (req.getParentCommentId() != null) {
            parentComment = getComment(req.getParentCommentId());
        }
        // TODO : post를 postService를 통해 읽어 온다.
        return commentRepository.save(Comment.builder()
            .post(postRepository.findById(req.getPostId()).orElseThrow(PostNotFoundException::new))
            .member(memberService.getMember(req.getMemberId())).content(req.getContent())
            .anonymity(req.getAnonymity()).parentComment(parentComment).build()).toDTO();
    }

    /**
     * 댓글을 수정하는 메서드
     * @param req 수정할 댓글 정보와 수정 내용이 담긴 객체
     * @return 수정한 댓글 DTO
     */
    public CommentDTO updateComment(UpdateCommentRequest req) {
        Comment comment = commentRepository.findById(req.getId())
            .orElseThrow(CommentNotFoundException::new);
        comment.updateContent(req.getContent());
        return comment.toDTO();
    }

    /**
     * 댓글을 삭제하는 메서드
     * @param req 삭제할 댓글 ID가 담긴 객체
     * @return 삭제한 댓글 DTO
     */
    public CommentDTO deleteComment(DeleteCommentRequest req) {
        Comment comment = commentRepository.findById(req.getId())
            .orElseThrow(CommentNotFoundException::new);
        comment.deleteComment();
        return comment.toDTO();
    }

    /**
     * 댓글 ID로 댓글 정보를 조회하는 메서드
     * @param id 조회할 댓글의 ID
     * @return 댓글 ID로 조회한 댓글 Entity
     */
    public Comment getComment(Long id) {
        return commentRepository.findById(id).orElseThrow(CommentNotFoundException::new);
    }

    /**
     * 게시글에 등록된 댓글 리스트를 조회하는 메서드
     * @param post 댓글 리스트를 조회할 게시글 Entity
     * @return 게시글에 등록된 댓글 Entity 리스트
     */
    public List<Comment> getCommentsByPost(Post post) {
        return commentRepository.findAllByPost(post).orElseGet(ArrayList::new);
    }

    /**
     * 회원이 등록한 댓글 리스트를 조회하는 메서드
     * @param member 댓글 리스트 조회의 기준이 될 회원 Entity
     * @return 회원이 등록한 댓글 Entity 리스트
     */
    public List<Comment> getCommentsByMember(Member member) {
        return commentRepository.findAllByMember(member).orElseGet(ArrayList::new);
    }
}
