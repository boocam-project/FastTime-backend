package com.fasttime.domain.review.exception;

import com.fasttime.global.exception.ApplicationException;
import com.fasttime.global.exception.ErrorCode;

public class ReviewAlreadyExistsException extends ApplicationException {

    private static final ErrorCode ERROR_CODE = ErrorCode.REVIEW_ALREADY_REGISTERED;

    public ReviewAlreadyExistsException() {
        super(ERROR_CODE);
    }
}
