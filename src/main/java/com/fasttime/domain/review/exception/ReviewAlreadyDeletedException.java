package com.fasttime.domain.review.exception;

import com.fasttime.global.exception.ApplicationException;
import com.fasttime.global.exception.ErrorCode;

public class ReviewAlreadyDeletedException extends ApplicationException {

    private static final ErrorCode ERROR_CODE = ErrorCode.ALREADY_DELETED_THIS_REVIEW;

    public ReviewAlreadyDeletedException() {
        super(ERROR_CODE);
    }
}
