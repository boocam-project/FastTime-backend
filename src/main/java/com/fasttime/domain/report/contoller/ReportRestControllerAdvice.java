package com.fasttime.domain.report.contoller;

import com.fasttime.domain.report.exception.DuplicateReportException;
import com.fasttime.global.util.ResponseDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
class ReportRestControllerAdvice {

    @ExceptionHandler
    ResponseEntity<ResponseDTO<Object>> duplicateReportException(DuplicateReportException e) {
        log.error("DuplicateReportException: " + e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(ResponseDTO.res(HttpStatus.BAD_REQUEST, e.getMessage()));
    }
}
