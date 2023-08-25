package com.fasttime.domain.report.exception;

import com.fasttime.global.exception.ApplicationException;
import org.springframework.http.HttpStatus;

public class DuplicateReportException extends ApplicationException {

    public DuplicateReportException(String message) {
        super(HttpStatus.BAD_REQUEST, message);
    }
}
