package com.fasttime.domain.post.exception;

import com.fasttime.global.exception.ApplicationException;
import com.fasttime.global.exception.ErrorCode;

public class PostNotFoundException extends ApplicationException {
    private static final ErrorCode ERROR_CODE = ErrorCode.POST_NOT_FOUND;

    public PostNotFoundException() {
        super(ERROR_CODE);
    }

    public PostNotFoundException(String message) {
        super(ERROR_CODE, message);
    }
}
