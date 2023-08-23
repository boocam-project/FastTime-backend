package com.fasttime.domain.comment.controller;

import com.fasttime.domain.comment.dto.request.CreateCommentRequest;
import com.fasttime.domain.comment.dto.request.DeleteCommentRequest;
import com.fasttime.domain.comment.dto.request.UpdateCommentRequest;
import com.fasttime.domain.comment.service.CommentService;
import com.fasttime.global.util.ResponseDTO;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

    @PostMapping("/create")
    public ResponseEntity<ResponseDTO> create(
        @Valid @RequestBody CreateCommentRequest createCommentRequest) {
        log.info("CreateCommentRequest: " + createCommentRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(
            ResponseDTO.res(HttpStatus.CREATED, "댓글을 성공적으로 등록했습니다.",
                commentService.createComment(createCommentRequest)));
    }

    @PostMapping("/delete")
    public ResponseEntity<ResponseDTO> delete(
        @Valid @RequestBody DeleteCommentRequest deleteCommentRequest) {
        log.info("DeleteCommentRequest: " + deleteCommentRequest);
        return ResponseEntity.status(HttpStatus.OK).body(
            ResponseDTO.res(HttpStatus.OK, "댓글을 성공적으로 삭제했습니다.",
                commentService.deleteComment(deleteCommentRequest)));
    }

    @PostMapping("/update")
    public ResponseEntity<ResponseDTO> update(
        @Valid @RequestBody UpdateCommentRequest updateCommentRequest) {
        log.info("UpdateCommentRequest: " + updateCommentRequest);
        return ResponseEntity.status(HttpStatus.OK)
            .body(ResponseDTO.res(HttpStatus.OK, "댓글 내용을 성공적으로 수정했습니다.",
                commentService.updateComment(updateCommentRequest)));
    }
}
