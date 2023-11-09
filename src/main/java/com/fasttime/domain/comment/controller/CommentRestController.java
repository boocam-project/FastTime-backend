package com.fasttime.domain.comment.controller;

import com.fasttime.domain.comment.dto.request.CommentPageRequestDTO;
import com.fasttime.domain.comment.dto.request.CreateCommentRequestDTO;
import com.fasttime.domain.comment.dto.request.GetCommentsRequestDTO;
import com.fasttime.domain.comment.dto.request.UpdateCommentRequestDTO;
import com.fasttime.domain.comment.dto.response.CommentListResponseDTO;
import com.fasttime.domain.comment.dto.response.CommentResponseDTO;
import com.fasttime.domain.comment.service.CommentService;
import com.fasttime.global.util.ResponseDTO;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/comments")
public class CommentRestController {

    private final CommentService commentService;

    @PostMapping("/{articleId}")
    public ResponseEntity<ResponseDTO<Object>> createComment(@PathVariable long articleId,
        @Valid @RequestBody CreateCommentRequestDTO createCommentRequestDTO, HttpSession session) {
        return ResponseEntity.status(HttpStatus.CREATED).body(
            ResponseDTO.res(HttpStatus.CREATED, "댓글을 성공적으로 등록했습니다.",
                commentService.createComment(articleId, (long) session.getAttribute("MEMBER"),
                    createCommentRequestDTO)));
    }

    @GetMapping
    public ResponseEntity<ResponseDTO<CommentListResponseDTO>> getComments(
        @RequestParam(required = false) Long articleId,
        @RequestParam(required = false) Long memberId,
        @RequestParam(required = false) Long parentCommentId,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int pageSize) {
        return ResponseEntity.status(HttpStatus.OK).body(
            ResponseDTO.res(HttpStatus.OK, "댓글을 성공적으로 조회했습니다.", commentService.getComments(
                GetCommentsRequestDTO.builder()
                    .articleId(articleId)
                    .memberId(memberId)
                    .parentCommentId(parentCommentId)
                    .build(),
                CommentPageRequestDTO.builder()
                    .page(page)
                    .size(pageSize)
                    .build().of()
            )));
    }

    @PatchMapping("/{commentId}")
    public ResponseEntity<ResponseDTO<CommentResponseDTO>> updateComment(
        @PathVariable long commentId,
        @Valid @RequestBody UpdateCommentRequestDTO updateCommentRequestDTO, HttpSession session) {
        return ResponseEntity.status(HttpStatus.OK).body(
            ResponseDTO.res(HttpStatus.OK, "댓글 내용을 성공적으로 수정했습니다.",
                commentService.updateComment(commentId, (long) session.getAttribute("MEMBER"),
                    updateCommentRequestDTO)));
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<ResponseDTO<CommentResponseDTO>> deleteComment(
        @PathVariable long commentId, HttpSession session) {
        return ResponseEntity.status(HttpStatus.OK).body(
            ResponseDTO.res(HttpStatus.OK, "댓글을 성공적으로 삭제했습니다.",
                commentService.deleteComment(commentId, (long) session.getAttribute("MEMBER"))));
    }
}
