package com.fasttime.domain.article.exception;

import com.fasttime.global.exception.ApplicationException;
import com.fasttime.global.exception.ErrorCode;

public class NotArticleWriterException extends ApplicationException {

    private static final ErrorCode ERROR_CODE = ErrorCode.HAS_NO_PERMISSION_WITH_THIS_ARTICLE;

    public NotArticleWriterException() {
        super(ERROR_CODE);
    }

    public NotArticleWriterException(String message) {
        super(ERROR_CODE, message);
    }
}
