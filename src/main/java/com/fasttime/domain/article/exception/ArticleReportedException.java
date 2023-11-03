package com.fasttime.domain.article.exception;

import com.fasttime.global.exception.ApplicationException;
import com.fasttime.global.exception.ErrorCode;

public class ArticleReportedException extends ApplicationException {

    private static final ErrorCode ERROR_CODE = ErrorCode.REPORT_ACCEPTED_ARTICLE;

    public ArticleReportedException() {
        super(ERROR_CODE);
    }

    public ArticleReportedException(String message) {
        super(ERROR_CODE, message);
    }
}
