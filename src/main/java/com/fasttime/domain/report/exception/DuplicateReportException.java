package com.fasttime.domain.report.exception;

import com.fasttime.global.exception.ApplicationException;
import com.fasttime.global.exception.ErrorCode;

public class DuplicateReportException extends ApplicationException {

    private static final ErrorCode ERROR_CODE = ErrorCode.ALREADY_REPORTED_THIS_POST;

    public DuplicateReportException() {
        super(ERROR_CODE);
    }

    public DuplicateReportException(String message) {
        super(ERROR_CODE, message);
    }
}
