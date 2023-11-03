package com.fasttime.domain.article.exception;

import com.fasttime.global.exception.ApplicationException;
import com.fasttime.global.exception.ErrorCode;

public class BadArticleReportStatusException extends ApplicationException {

    private static final ErrorCode ERROR_CODE = ErrorCode.BAD_REPORT_STATUS;

    public BadArticleReportStatusException() {
        super(ERROR_CODE);
    }
}
