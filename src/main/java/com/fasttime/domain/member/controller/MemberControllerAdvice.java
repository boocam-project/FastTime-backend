package com.fasttime.domain.member.controller;

import com.fasttime.domain.member.exception.UserNotFoundException;
import java.util.HashMap;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class MemberControllerAdvice {

    @ExceptionHandler   //검증 실패 예외
    public ResponseEntity<?> bindException(BindException e) {
        Map<String, Object> message = new HashMap<>();
        message.put("error", e.getBindingResult().getAllErrors().get(0).getDefaultMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .contentType(MediaType.APPLICATION_JSON)
            .body(message);
    }
    @ExceptionHandler   // 비밀번호가 틀리거나 비밀번호 재확인이 일치하지않는 예외
    public ResponseEntity<?> BadCredentialsException(BadCredentialsException e) {
        Map<String, Object> message = new HashMap<>();
        message.put("error", e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .contentType(MediaType.APPLICATION_JSON)
            .body(message);
    }
    @ExceptionHandler //등록되지 않는 이메일로 접근시 발생하는 예외
    public ResponseEntity<?> UserNotFoundException(UserNotFoundException e) {
        Map<String, Object> message = new HashMap<>();
        message.put("error", e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .contentType(MediaType.APPLICATION_JSON)
            .body(message);
    }
}
