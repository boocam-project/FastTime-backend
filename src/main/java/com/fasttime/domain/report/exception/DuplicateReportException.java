package com.fasttime.domain.report.exception;

import com.fasttime.global.exception.ApplicationException;
import org.springframework.http.HttpStatus;

public class DuplicateReportException extends ApplicationException {

    public DuplicateReportException() {
        super(HttpStatus.BAD_REQUEST, "이미 신고한 게시글입니다.");
    }
}
