package com.fasttime.domain.comment.controller;

import com.fasttime.domain.comment.exception.NotCommentAuthorException;
import com.fasttime.global.util.ResponseDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class CommentRestControllerAdvice {

    @ExceptionHandler
    public ResponseEntity<ResponseDTO<Object>> notCommentAuthorException(
        NotCommentAuthorException e) {
        log.error(e.getMessage(), e);
        return ResponseEntity.status(e.getErrorCode().getHttpStatus())
            .body(ResponseDTO.res(e.getErrorCode().getHttpStatus(), e.getErrorCode().getMessage()));

    }
}
