package com.fasttime.domain.comment.controller;

import com.fasttime.domain.comment.exception.NotFoundException;
import com.fasttime.global.util.ResponseDTO;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class CommentRestControllerAdvice {

    @ExceptionHandler
    public ResponseEntity<?> notFoundException(NotFoundException e) {
        log.error("NotFoundException: " + e.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(ResponseDTO.res(HttpStatus.NOT_FOUND, e.getMessage()));
    }

    @ExceptionHandler
    public ResponseEntity<?> bindException(BindException e) {
        log.error("BindException: " + e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
            ResponseDTO.res(HttpStatus.BAD_REQUEST,
                e.getBindingResult().getAllErrors().get(0).getDefaultMessage()));
    }

    @ExceptionHandler
    public ResponseEntity<?> illegalArgException(IllegalArgumentException e) {
        log.error("IllegalArgumentException: " + e.getMessage());
        Map<String, Object> errorMessage = new HashMap<>();
        errorMessage.put("status", 400);
        errorMessage.put("error", e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(ResponseDTO.res(HttpStatus.BAD_REQUEST, e.getMessage()));
    }
}
