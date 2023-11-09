package com.fasttime.domain.comment.service;

import com.fasttime.domain.article.entity.Article;
import com.fasttime.domain.article.service.ArticleQueryService;
import com.fasttime.domain.comment.dto.request.CreateCommentRequestDTO;
import com.fasttime.domain.comment.dto.request.GetCommentsRequestDTO;
import com.fasttime.domain.comment.dto.request.UpdateCommentRequestDTO;
import com.fasttime.domain.comment.dto.response.CommentListResponseDTO;
import com.fasttime.domain.comment.dto.response.CommentResponseDTO;
import com.fasttime.domain.comment.entity.Comment;
import com.fasttime.domain.comment.exception.CommentNotFoundException;
import com.fasttime.domain.comment.exception.MultipleSearchConditionException;
import com.fasttime.domain.comment.exception.NotCommentAuthorException;
import com.fasttime.domain.comment.repository.CommentRepository;
import com.fasttime.domain.member.service.MemberService;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import javax.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final ArticleQueryService postQueryService;
    private final MemberService memberService;

    public CommentResponseDTO createComment(long articleId, long memberId,
        CreateCommentRequestDTO createCommentRequestDTO) {
        boolean isChildComment = createCommentRequestDTO.getParentCommentId() != null;
        Comment parentComment =
            isChildComment ? getComment(createCommentRequestDTO.getParentCommentId()) : null;
        return commentRepository.save(Comment.builder()
                .article(Article.builder()
                    .id(postQueryService.queryById(articleId).getId())
                    .build())
                .member(memberService.getMember(memberId))
                .content(createCommentRequestDTO.getContent())
                .anonymity(createCommentRequestDTO.getAnonymity())
                .parentComment(parentComment)
                .build())
            .toCommentResponseDTO();
    }

    public CommentListResponseDTO getComments(GetCommentsRequestDTO getCommentsRequestDTO,
        Pageable pageable) {
        checkDuplicateCondition(getCommentsRequestDTO);
        List<CommentResponseDTO> comments = new ArrayList<>();
        Page<Comment> commentsFromDB = commentRepository.findAllBySearchCondition(
            getCommentsRequestDTO, pageable);
        commentsFromDB.forEach(comment -> comments.add(comment.toCommentResponseDTO()));
        return CommentListResponseDTO.builder()
            .totalPages(commentsFromDB.getTotalPages())
            .isLastPage(commentsFromDB.isLast())
            .totalComments(getTotalComments(getCommentsRequestDTO, commentsFromDB))
            .comments(comments)
            .build();
    }

    public CommentResponseDTO updateComment(long commentId, long memberId,
        UpdateCommentRequestDTO updateCommentRequestDTO) {
        Comment comment = getComment(commentId);
        if (memberId != comment.getMember().getId()) {
            throw new NotCommentAuthorException();
        }
        comment.updateContent(updateCommentRequestDTO.getContent());
        return comment.toCommentResponseDTO();
    }

    public CommentResponseDTO deleteComment(long commentId, long memberId) {
        Comment comment = getComment(commentId);
        if (memberId != comment.getMember().getId()) {
            throw new NotCommentAuthorException();
        }
        comment.delete(LocalDateTime.now());
        return comment.toCommentResponseDTO();
    }

    public Comment getComment(Long id) {
        return commentRepository.findById(id).orElseThrow(CommentNotFoundException::new);
    }

    private void checkDuplicateCondition(GetCommentsRequestDTO getCommentsRequestDTO) {
        int count = 0;
        if (isFindByArticleId(getCommentsRequestDTO)) {
            count++;
        }
        if (isFindByMemberId(getCommentsRequestDTO)) {
            count++;
        }
        if (isFindByParentCommentId(getCommentsRequestDTO)) {
            count++;
        }
        if (count > 1) {
            throw new MultipleSearchConditionException();
        }
    }

    private long getTotalComments(GetCommentsRequestDTO getCommentsRequestDTO,
        Page<Comment> commentsFromDB) {
        return isFindByArticleId(getCommentsRequestDTO) ? commentRepository.countByArticleId(
            getCommentsRequestDTO.getArticleId()) : commentsFromDB.getTotalElements();
    }

    private boolean isFindByArticleId(GetCommentsRequestDTO getCommentsRequestDTO) {
        return getCommentsRequestDTO.getArticleId() != null;
    }

    private boolean isFindByMemberId(GetCommentsRequestDTO getCommentsRequestDTO) {
        return getCommentsRequestDTO.getMemberId() != null;
    }

    private boolean isFindByParentCommentId(GetCommentsRequestDTO getCommentsRequestDTO) {
        return getCommentsRequestDTO.getParentCommentId() != null;
    }
}
