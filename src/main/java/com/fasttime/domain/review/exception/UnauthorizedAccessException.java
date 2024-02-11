package com.fasttime.domain.review.exception;

import com.fasttime.global.exception.ApplicationException;
import com.fasttime.global.exception.ErrorCode;

public class UnauthorizedAccessException extends ApplicationException {

    private static final ErrorCode ERROR_CODE = ErrorCode.HAS_NO_PERMISSION_WITH_THIS_REVIEW;

    public UnauthorizedAccessException() {
        super(ERROR_CODE);
    }
}