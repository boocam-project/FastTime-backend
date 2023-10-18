package com.fasttime.domain.comment.controller;

import com.fasttime.domain.comment.dto.request.CreateCommentRequest;
import com.fasttime.domain.comment.dto.request.DeleteCommentRequest;
import com.fasttime.domain.comment.dto.request.UpdateCommentRequest;
import com.fasttime.domain.comment.dto.response.MyPageCommentResponseDTO;
import com.fasttime.domain.comment.dto.response.PostCommentResponseDTO;
import com.fasttime.domain.comment.service.CommentService;
import com.fasttime.global.util.ResponseDTO;
import java.util.List;
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
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/comment")
public class CommentRestController {

    private final CommentService commentService;

    @PostMapping
    public ResponseEntity<ResponseDTO<PostCommentResponseDTO>> createComment(
        @Valid @RequestBody CreateCommentRequest createCommentRequest) {
        log.info("CreateCommentRequest: " + createCommentRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(
            ResponseDTO.res(HttpStatus.CREATED, "댓글을 성공적으로 등록했습니다.",
                commentService.createComment(createCommentRequest)));
    }

    @GetMapping("/my-page/{memberId}")
    public ResponseEntity<ResponseDTO<List<MyPageCommentResponseDTO>>> getCommentsByMemberId(
        @PathVariable long memberId) {
        log.info("getCommentsByMemberId: " + memberId);
        return ResponseEntity.status(HttpStatus.OK).body(
            ResponseDTO.res(HttpStatus.OK, "댓글을 성공적으로 조회했습니다.",
                commentService.getCommentsByMemberId(memberId)));
    }

    @GetMapping("/{postId}")
    public ResponseEntity<ResponseDTO<List<PostCommentResponseDTO>>> getCommentsByPostId(
        @PathVariable long postId) {
        log.info("getCommentsByPostId: " + postId);
        return ResponseEntity.status(HttpStatus.OK).body(
            ResponseDTO.res(HttpStatus.OK, "댓글을 성공적으로 조회했습니다.",
                commentService.getCommentsByPostId(postId)));
    }

    @PatchMapping
    public ResponseEntity<ResponseDTO<PostCommentResponseDTO>> updateComment(
        @Valid @RequestBody UpdateCommentRequest updateCommentRequest) {
        log.info("UpdateCommentRequest: " + updateCommentRequest);
        return ResponseEntity.status(HttpStatus.OK).body(
            ResponseDTO.res(HttpStatus.OK, "댓글 내용을 성공적으로 수정했습니다.",
                commentService.updateComment(updateCommentRequest)));
    }

    @DeleteMapping
    public ResponseEntity<ResponseDTO<Void>> deleteComment(
        @Valid @RequestBody DeleteCommentRequest deleteCommentRequest) {
        log.info("DeleteCommentRequest: " + deleteCommentRequest);
        commentService.deleteComment(deleteCommentRequest);
        return ResponseEntity.status(HttpStatus.OK)
            .body(ResponseDTO.res(HttpStatus.OK, "댓글을 성공적으로 삭제했습니다.", null));
    }
}
