package com.fasttime.domain.comment.controller;

import com.fasttime.domain.comment.dto.request.CreateCommentRequestDTO;
import com.fasttime.domain.comment.dto.request.DeleteCommentRequestDTO;
import com.fasttime.domain.comment.dto.request.GetCommentsRequestDTO;
import com.fasttime.domain.comment.dto.request.UpdateCommentRequestDTO;
import com.fasttime.domain.comment.dto.response.CommentResponseDTO;
import com.fasttime.domain.comment.service.CommentService;
import com.fasttime.global.util.ResponseDTO;
import java.util.List;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
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

    @PostMapping
    public ResponseEntity<ResponseDTO<Object>> createComment(
        @Valid @RequestBody CreateCommentRequestDTO createCommentRequestDTO, HttpSession session) {
        log.info("CreateCommentRequest: " + createCommentRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(
            ResponseDTO.res(HttpStatus.CREATED, "댓글을 성공적으로 등록했습니다.",
                commentService.createComment(createCommentRequestDTO,
                    (Long) session.getAttribute("MEMBER"))));
    }

    @GetMapping
    public ResponseEntity<ResponseDTO<List<CommentResponseDTO>>> getComments(
        @RequestParam(required = false) Long articleId,
        @RequestParam(required = false) Long memberId,
        @RequestParam(required = false) Long parentCommentId,
        @RequestParam(defaultValue = "10") int pageSize,
        @RequestParam(defaultValue = "0") int page) {
        log.info("getComments");
        return ResponseEntity.status(HttpStatus.OK).body(
            ResponseDTO.res(HttpStatus.OK, "댓글을 성공적으로 조회했습니다.", commentService.getComments(
                GetCommentsRequestDTO.builder().articleId(articleId).memberId(memberId)
                    .parentCommentId(parentCommentId).pageSize(pageSize).page(page).build())));
    }

    @PatchMapping
    public ResponseEntity<ResponseDTO<Object>> updateComment(
        @Valid @RequestBody UpdateCommentRequestDTO updateCommentRequestDTO) {
        log.info("UpdateCommentRequest: " + updateCommentRequestDTO);
        return ResponseEntity.status(HttpStatus.OK).body(
            ResponseDTO.res(HttpStatus.OK, "댓글 내용을 성공적으로 수정했습니다.",
                commentService.updateComment(updateCommentRequestDTO)));
    }

    @DeleteMapping
    public ResponseEntity<ResponseDTO<Object>> deleteComment(
        @Valid @RequestBody DeleteCommentRequestDTO deleteCommentRequestDTO) {
        log.info("DeleteCommentRequest: " + deleteCommentRequestDTO);
        return ResponseEntity.status(HttpStatus.OK).body(
            ResponseDTO.res(HttpStatus.OK, "댓글을 성공적으로 삭제했습니다.",
                commentService.deleteComment(deleteCommentRequestDTO)));
    }
}
