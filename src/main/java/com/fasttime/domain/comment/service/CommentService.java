package com.fasttime.domain.comment.service;

import com.fasttime.domain.comment.dto.request.CreateCommentRequestDTO;
import com.fasttime.domain.comment.dto.request.DeleteCommentRequestDTO;
import com.fasttime.domain.comment.dto.request.UpdateCommentRequestDTO;
import com.fasttime.domain.comment.dto.response.MyPageCommentResponseDTO;
import com.fasttime.domain.comment.dto.response.PostCommentResponseDTO;
import com.fasttime.domain.comment.entity.Comment;
import com.fasttime.domain.comment.exception.CommentNotFoundException;
import com.fasttime.domain.comment.repository.CommentRepository;
import com.fasttime.domain.member.entity.Member;
import com.fasttime.domain.member.service.MemberService;
import com.fasttime.domain.article.dto.service.response.ArticleResponse;
import com.fasttime.domain.article.entity.Article;
import com.fasttime.domain.article.service.ArticleQueryService;
import java.time.LocalDateTime;
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
    private final ArticleQueryService postQueryService;
    private final MemberService memberService;

    public void createComment(CreateCommentRequestDTO createCommentRequestDTO, Long memberId) {
        Comment parentComment = isCommentOfComment(createCommentRequestDTO) ? getComment(
            createCommentRequestDTO.getParentCommentId()) : null;
        commentRepository.save(Comment.builder().article(Article.builder()
                    .id(postQueryService.queryById(createCommentRequestDTO.getPostId()).getId()).build())
                .member(memberService.getMember(memberId)).content(createCommentRequestDTO.getContent())
                .anonymity(createCommentRequestDTO.getAnonymity()).parentComment(parentComment).build())
            .toPostCommentResponseDTO();
    }

    public List<MyPageCommentResponseDTO> getCommentsByMemberId(Long memberId) {
        return getCommentsByMember(memberService.getMember(memberId));
    }

    public List<PostCommentResponseDTO> getCommentsByPostId(long postId) {
        ArticleResponse postResponse = postQueryService.queryById(postId);
        return getCommentsByPost(Article.builder().id(postResponse.getId()).build());
    }

    public void updateComment(UpdateCommentRequestDTO updateCommentRequestDTO) {
        Comment comment = getComment(updateCommentRequestDTO.getId());
        comment.updateContent(updateCommentRequestDTO.getContent());
    }

    public void deleteComment(DeleteCommentRequestDTO deleteCommentRequestDTO) {
        Comment comment = getComment(deleteCommentRequestDTO.getId());
        comment.delete(LocalDateTime.now());
    }

    public Comment getComment(Long id) {
        return commentRepository.findById(id).orElseThrow(CommentNotFoundException::new);
    }

    public List<PostCommentResponseDTO> getCommentsByPost(Article post) {
        List<PostCommentResponseDTO> comments = new ArrayList<>();
        List<Comment> list = commentRepository.findAllByArticle(post).orElseGet(ArrayList::new);
        for (Comment comment : list) {
            comments.add(comment.toPostCommentResponseDTO());
        }
        return comments;
    }

    public List<MyPageCommentResponseDTO> getCommentsByMember(Member member) {
        List<MyPageCommentResponseDTO> comments = new ArrayList<>();
        List<Comment> list = commentRepository.findAllByMember(member).orElseGet(ArrayList::new);
        for (Comment comment : list) {
            comments.add(comment.toMyPageCommentResponseDTO());
        }
        return comments;
    }

    private boolean isCommentOfComment(CreateCommentRequestDTO createCommentRequestDTO) {
        return createCommentRequestDTO.getParentCommentId() != null;
    }
}
