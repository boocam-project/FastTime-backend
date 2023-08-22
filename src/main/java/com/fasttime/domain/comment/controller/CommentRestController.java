package com.fasttime.domain.comment.controller;

import com.fasttime.domain.comment.dto.request.CreateCommentRequest;
import com.fasttime.domain.comment.dto.request.DeleteCommentRequest;
import com.fasttime.domain.comment.dto.request.UpdateCommentRequest;
import com.fasttime.domain.comment.service.CommentService;
import java.util.HashMap;
import java.util.Map;
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
    public ResponseEntity<Map<String, Object>> create(
        @Valid @RequestBody CreateCommentRequest createCommentRequest) {
        log.info("CreateCommentRequest: " + createCommentRequest);
        Map<String, Object> message = new HashMap<>();
        message.put("status", 201);
        message.put("data", commentService.createComment(createCommentRequest));

        return ResponseEntity.status(HttpStatus.CREATED).body(message);
    }

    @PostMapping("/delete")
    public ResponseEntity<Map<String, Object>> delete(
        @Valid @RequestBody DeleteCommentRequest deleteCommentRequest) {
        log.info("DeleteCommentRequest: " + deleteCommentRequest);
        Map<String, Object> message = new HashMap<>();
        message.put("status", 200);
        message.put("data", commentService.deleteComment(deleteCommentRequest));
        return ResponseEntity.status(HttpStatus.OK).body(message);
    }

    @PostMapping("/update")
    public ResponseEntity<Map<String, Object>> update(
        @Valid @RequestBody UpdateCommentRequest updateCommentRequest) {
        log.info("UpdateCommentRequest: " + updateCommentRequest);
        Map<String, Object> message = new HashMap<>();
        message.put("status", 200);
        message.put("data", commentService.updateComment(updateCommentRequest));
        return ResponseEntity.status(HttpStatus.OK).body(message);
    }
}
