package com.fasttime.domain.member.controller;

import com.fasttime.domain.comment.exception.NotCommentAuthorException;
import com.fasttime.domain.member.exception.UserNotFoundException;
import com.fasttime.domain.member.exception.UserNotMatchInfoException;
import com.fasttime.domain.member.exception.UserNotMatchRePasswordException;
import com.fasttime.domain.member.exception.UserSoftDeletedException;
import com.fasttime.global.util.ResponseDTO;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
@Slf4j
@RestControllerAdvice(basePackages = "com.fasttime.domain.member")
public class MemberControllerAdvice {

    @ExceptionHandler
    public ResponseEntity<ResponseDTO<Object>> badCredentialsException(BadCredentialsException e) {
        Map<String, Object> message = new HashMap<>();
        message.put("error", e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .contentType(MediaType.APPLICATION_JSON)
            .body(ResponseDTO.res(HttpStatus.BAD_REQUEST, message));
    }
    @ExceptionHandler
    public ResponseEntity<ResponseDTO> userNotFoundException(UserNotFoundException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
            ResponseDTO.res(HttpStatus.BAD_REQUEST,
                e.getMessage()));
    }
    @ExceptionHandler
    public ResponseEntity<ResponseDTO<Object>> userNotMatchInfoException(UserNotMatchInfoException e){
        log.error("userNotMatchInfoException: ",e.getMessage());
        return ResponseEntity.status(e.getErrorCode().getHttpStatus())
            .body(ResponseDTO.res(e.getErrorCode().getHttpStatus(), e.getErrorCode().getMessage()));
    }
    @ExceptionHandler
    public ResponseEntity<ResponseDTO<Object>> userSoftDeletedException(UserSoftDeletedException e){
        log.error("userSoftDeletedException: ",e.getMessage());
        return ResponseEntity.status(e.getErrorCode().getHttpStatus())
            .body(ResponseDTO.res(e.getErrorCode().getHttpStatus(), e.getErrorCode().getMessage()));
    }
    @ExceptionHandler
    public ResponseEntity<ResponseDTO<Object>> userNotMatchRePasswordException(
        UserNotMatchRePasswordException e){
        log.error("userNotMatchRePasswordException: ",e.getMessage());
        return ResponseEntity.status(e.getErrorCode().getHttpStatus())
            .body(ResponseDTO.res(e.getErrorCode().getHttpStatus(), e.getErrorCode().getMessage()));
    }
}


