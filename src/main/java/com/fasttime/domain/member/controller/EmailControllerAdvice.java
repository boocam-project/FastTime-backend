package com.fasttime.domain.member.controller;

import com.fasttime.global.util.ResponseDTO;
import java.util.HashMap;
import java.util.Map;
import javax.naming.AuthenticationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class EmailControllerAdvice {
    @ExceptionHandler   // 코드가 일치하지않는 예외
    public ResponseEntity<ResponseDTO> BadCredentialsException(BadCredentialsException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
            ResponseDTO.res(HttpStatus.BAD_REQUEST,
                e.getMessage()));
    }
}
