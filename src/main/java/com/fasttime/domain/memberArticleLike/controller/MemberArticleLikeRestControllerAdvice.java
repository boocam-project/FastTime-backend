package com.fasttime.domain.memberArticleLike.controller;

import com.fasttime.domain.memberArticleLike.exception.AlreadyExistsMemberArticleLikeException;
import com.fasttime.domain.memberArticleLike.exception.DuplicateMemberArticleLikeException;
import com.fasttime.global.util.ResponseDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
class MemberArticleLikeRestControllerAdvice {

    @ExceptionHandler
    ResponseEntity<ResponseDTO<Object>> duplicateRecordException(
        DuplicateMemberArticleLikeException e) {
        log.error("DuplicateMemberArticleLikeException: " + e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(ResponseDTO.res(HttpStatus.BAD_REQUEST, e.getMessage()));
    }

    @ExceptionHandler
    ResponseEntity<ResponseDTO<Object>> alreadyExistsRecordException(
        AlreadyExistsMemberArticleLikeException e) {
        log.error("AlreadyExistsMemberArticleLikeException: " + e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(ResponseDTO.res(HttpStatus.BAD_REQUEST, e.getMessage()));
    }
}
