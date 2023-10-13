package com.fasttime.domain.member.controller;

import com.fasttime.domain.member.exception.UserNotFoundException;
import com.fasttime.global.util.ResponseDTO;
import java.util.HashMap;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(basePackages = "com.fasttime.domain.member")
public class MemberControllerAdvice {

    @ExceptionHandler   // 비밀번호가 틀리거나 비밀번호 재확인이 일치하지않는 예외
    public ResponseEntity<ResponseDTO<Object>> badCredentialsException(BadCredentialsException e) {
        Map<String, Object> message = new HashMap<>();
        message.put("error", e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .contentType(MediaType.APPLICATION_JSON)
            .body(ResponseDTO.res(HttpStatus.BAD_REQUEST, message));
    }
    @ExceptionHandler //등록되지 않는 이메일로 접근시 발생하는 예외
    public ResponseEntity<ResponseDTO> userNotFoundException(UserNotFoundException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
            ResponseDTO.res(HttpStatus.BAD_REQUEST,
                e.getMessage()));
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ResponseDTO> handleUserNotFoundException(UserNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(ResponseDTO.res(HttpStatus.NOT_FOUND, e.getMessage()));
    }
}
