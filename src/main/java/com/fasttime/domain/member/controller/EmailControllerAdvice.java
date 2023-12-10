package com.fasttime.domain.member.controller;

import com.fasttime.domain.member.exception.EmailSendingException;
import com.fasttime.domain.member.exception.EmailTemplateLoadException;
import com.fasttime.domain.member.exception.NicknameAlreadyExistsException;
import com.fasttime.global.util.ResponseDTO;
import java.util.HashMap;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class EmailControllerAdvice {

    @ExceptionHandler
    public ResponseEntity<ResponseDTO<Object>> badCredentialsException(BadCredentialsException e) {
        Map<String, Object> message = new HashMap<>();
        message.put("error", e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .contentType(MediaType.APPLICATION_JSON)
            .body(ResponseDTO.res(HttpStatus.BAD_REQUEST, message));
    }

    @ExceptionHandler(EmailSendingException.class)
    public ResponseEntity<ResponseDTO<Object>> handleEmailSendingException(
        NicknameAlreadyExistsException e) {
        return ResponseEntity
            .status(e.getErrorCode().getHttpStatus())
            .body(ResponseDTO.res(e.getErrorCode().getHttpStatus(), e.getErrorCode().getMessage()));
    }

    @ExceptionHandler(EmailTemplateLoadException.class)
    public ResponseEntity<ResponseDTO<Object>> handleEmailTemplateLoadException(
        NicknameAlreadyExistsException e) {
        return ResponseEntity
            .status(e.getErrorCode().getHttpStatus())
            .body(ResponseDTO.res(e.getErrorCode().getHttpStatus(), e.getErrorCode().getMessage()));
    }


}
