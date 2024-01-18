package com.fasttime.domain.review.exception;

import com.fasttime.global.exception.ApplicationException;
import com.fasttime.global.exception.ErrorCode;

public class TagNotFoundException extends ApplicationException {

    private static final ErrorCode ERROR_CODE = ErrorCode.TAG_NOT_FOUND;

    public TagNotFoundException() {
        super(ERROR_CODE);
    }

}
