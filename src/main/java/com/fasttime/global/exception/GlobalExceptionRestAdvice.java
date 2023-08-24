package com.fasttime.global.exception;

import com.fasttime.global.util.ResponseDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionRestAdvice {

    @ExceptionHandler
    public ResponseEntity<ResponseDTO<Object>> applicationException(ApplicationException e) {
        log.error("applicationException: " + e.getMessage());
        return ResponseEntity
            .status(e.getHttpStatus())
            .body(ResponseDTO.res(e.getHttpStatus(), e.getMessage()));
    }

    @ExceptionHandler
    public ResponseEntity<ResponseDTO<Object>> bindException(BindException e) {
        log.error("BindException: " + e.getMessage());
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(ResponseDTO.res(HttpStatus.BAD_REQUEST,
                e.getBindingResult().getAllErrors().get(0).getDefaultMessage()));
    }

    @ExceptionHandler
    public ResponseEntity<ResponseDTO<Object>> serverException(RuntimeException e) {
        log.error("Server Exception: " + e.getMessage());
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(ResponseDTO.res(HttpStatus.INTERNAL_SERVER_ERROR, "서버 에러!"));
    }
}
