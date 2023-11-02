package com.fasttime.domain.comment.service;

import com.fasttime.domain.article.entity.Article;
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
import com.fasttime.domain.member.service.MemberService;
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
    private final CommentCustomRepository commentCustomRepository;
    private final ArticleQueryService postQueryService;
    private final MemberService memberService;

    public CommentResponseDTO createComment(CreateCommentRequestDTO createCommentRequestDTO, Long memberId) {
        Comment parentComment = isCommentOfComment(createCommentRequestDTO) ? getComment(
            createCommentRequestDTO.getParentCommentId()) : null;
        return commentRepository.save(Comment.builder().article(Article.builder()
                    .id(postQueryService.queryById(createCommentRequestDTO.getPostId()).getId()).build())
                .member(memberService.getMember(memberId)).content(createCommentRequestDTO.getContent())
                .anonymity(createCommentRequestDTO.getAnonymity()).parentComment(parentComment).build())
            .toCommentResponseDTO();
    }

    public List<CommentResponseDTO> getComments(
        GetCommentsRequestDTO getCommentsRequestDTO) {
        List<CommentResponseDTO> comments = new ArrayList<>();
        commentCustomRepository.findAllBySearchCondition(getCommentsRequestDTO)
            .forEach(comment -> {
                comments.add(comment.toCommentResponseDTO());
            });
        return comments;
    }

    public CommentResponseDTO updateComment(UpdateCommentRequestDTO updateCommentRequestDTO) {
        Comment comment = getComment(updateCommentRequestDTO.getId());
        comment.updateContent(updateCommentRequestDTO.getContent());
        return comment.toCommentResponseDTO();
    }

    public CommentResponseDTO deleteComment(DeleteCommentRequestDTO deleteCommentRequestDTO) {
        Comment comment = getComment(deleteCommentRequestDTO.getId());
        comment.delete(LocalDateTime.now());
        return comment.toCommentResponseDTO();
    }

    public Comment getComment(Long id) {
        return commentRepository.findById(id).orElseThrow(CommentNotFoundException::new);
    }

    private boolean isCommentOfComment(CreateCommentRequestDTO createCommentRequestDTO) {
        return createCommentRequestDTO.getParentCommentId() != null;
    }
}
