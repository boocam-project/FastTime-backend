package com.fasttime.domain.post.exception;

import com.fasttime.global.exception.ApplicationException;
import com.fasttime.global.exception.ErrorCode;

public class PostReportedException extends ApplicationException {

    private static final ErrorCode ERROR_CODE = ErrorCode.POST_IS_REPORTED;

    public PostReportedException() {
        super(ERROR_CODE);
    }

    public PostReportedException(String message) {
        super(ERROR_CODE, message);
    }
}
