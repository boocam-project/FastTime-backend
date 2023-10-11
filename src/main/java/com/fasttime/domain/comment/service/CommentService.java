package com.fasttime.domain.comment.service;

import com.fasttime.domain.comment.dto.request.CreateCommentRequest;
import com.fasttime.domain.comment.dto.request.DeleteCommentRequest;
import com.fasttime.domain.comment.dto.request.UpdateCommentRequest;
import com.fasttime.domain.comment.dto.response.MyPageCommentResponseDTO;
import com.fasttime.domain.comment.dto.response.PostCommentResponseDTO;
import com.fasttime.domain.comment.entity.Comment;
import com.fasttime.domain.comment.exception.CommentNotFoundException;
import com.fasttime.domain.comment.repository.CommentRepository;
import com.fasttime.domain.member.entity.Member;
import com.fasttime.domain.member.service.MemberService;
import com.fasttime.domain.post.entity.Post;
import com.fasttime.domain.post.service.PostQueryService;
import java.util.ArrayList;
import java.util.List;
import javax.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostQueryService postQueryService;
    private final MemberService memberService;

    /**
     * 댓글을 등록하는 메서드
     *
     * @param req 등록할 댓글 정보가 담긴 객체
     * @return 등록한 댓글 DTO
     */
    public PostCommentResponseDTO createComment(CreateCommentRequest req) {
        Comment parentComment = null;
        // 대댓글인 경우
        if (req.getParentCommentId() != null) {
            parentComment = getComment(req.getParentCommentId());
        }
        return commentRepository.save(
                Comment.builder().post(postQueryService.findById(req.getPostId()))
                    .member(memberService.getMember(req.getMemberId())).content(req.getContent())
                    .anonymity(req.getAnonymity()).parentComment(parentComment).build())
            .toPostCommentResponseDTO();
    }

    /**
     * 회원 ID로 회원이 등록한 댓글 리스트를 조회하는 메서드
     *
     * @param memberId 댓글 리스트 조회의 기준이 될 회원 ID
     * @return 회원이 등록한 댓글 DTO 리스트
     */
    public List<MyPageCommentResponseDTO> getCommentsByMemberId(long memberId) {
        return getCommentsByMember(memberService.getMember(memberId));
    }

    /**
     * 게시글 ID로 해당 게시글에 등록된 댓글 리스트를 조회하는 메서드
     *
     * @param postId 댓글 리스트 조회의 기준이 될 게시글 ID
     * @return 게시글에 등록된 댓글 DTO 리스트
     */
    public List<PostCommentResponseDTO> getCommentsByPostId(long postId) {
        return getCommentsByPost(postQueryService.findById(postId));
    }

    /**
     * 댓글을 수정하는 메서드
     *
     * @param req 수정할 댓글 정보와 수정 내용이 담긴 객체
     * @return 수정한 댓글 DTO
     */
    public PostCommentResponseDTO updateComment(UpdateCommentRequest req) {
        Comment comment = commentRepository.findById(req.getId())
            .orElseThrow(CommentNotFoundException::new);
        comment.updateContent(req.getContent());
        return comment.toPostCommentResponseDTO();
    }

    /**
     * 댓글을 삭제하는 메서드
     *
     * @param req 삭제할 댓글 ID가 담긴 객체
     * @return 삭제한 댓글 DTO
     */
    public void deleteComment(DeleteCommentRequest req) {
        Comment comment = commentRepository.findById(req.getId())
            .orElseThrow(CommentNotFoundException::new);
        comment.deleteComment();
    }

    /**
     * 댓글 ID로 댓글 정보를 조회하는 메서드
     *
     * @param id 조회할 댓글의 ID
     * @return 댓글 ID로 조회한 댓글 Entity
     */
    public Comment getComment(Long id) {
        return commentRepository.findById(id).orElseThrow(CommentNotFoundException::new);
    }

    /**
     * 게시글에 등록된 댓글 리스트를 조회하는 메서드
     *
     * @param post 댓글 리스트를 조회할 게시글 Entity
     * @return 게시글에 등록된 댓글 리스트
     */
    public List<PostCommentResponseDTO> getCommentsByPost(Post post) {
        List<PostCommentResponseDTO> comments = new ArrayList<>();
        List<Comment> list = commentRepository.findAllByPost(post).orElseGet(ArrayList::new);
        for (Comment comment : list) {
            comments.add(comment.toPostCommentResponseDTO());
        }
        return comments;
    }

    /**
     * 회원이 등록한 댓글 리스트를 조회하는 메서드
     *
     * @param member 댓글 리스트 조회의 기준이 될 회원 Entity
     * @return 회원이 등록한 댓글 리스트
     */
    public List<MyPageCommentResponseDTO> getCommentsByMember(Member member) {
        List<MyPageCommentResponseDTO> comments = new ArrayList<>();
        List<Comment> list = commentRepository.findAllByMember(member).orElseGet(ArrayList::new);
        for (Comment comment : list) {
            comments.add(comment.toMyPageCommentResponseDTO());
        }
        return comments;
    }
}
