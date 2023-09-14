package com.fasttime.domain.member.controller;

import com.fasttime.global.util.ResponseDTO;
import java.rmi.AccessException;
import java.util.HashMap;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class AdminControllerAdvice {

    @ExceptionHandler
    public ResponseEntity<ResponseDTO> IllegalArgumentException(IllegalArgumentException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResponseDTO.res
            (HttpStatus.BAD_REQUEST, e.getMessage()));
    }
    @ExceptionHandler
    public ResponseEntity<ResponseDTO> AccessException(AccessException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResponseDTO.res
            (HttpStatus.BAD_REQUEST, e.getMessage()));
    }

}
