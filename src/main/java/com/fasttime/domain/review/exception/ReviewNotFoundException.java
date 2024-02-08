package com.fasttime.domain.review.exception;

import com.fasttime.global.exception.ApplicationException;
import com.fasttime.global.exception.ErrorCode;

public class ReviewNotFoundException extends ApplicationException {

    private static final ErrorCode ERROR_CODE = ErrorCode.REVIEW_NOT_FOUND;

    public ReviewNotFoundException() {
        super(ERROR_CODE);
    }
}
