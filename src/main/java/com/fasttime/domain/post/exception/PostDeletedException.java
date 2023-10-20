package com.fasttime.domain.post.exception;

import com.fasttime.global.exception.ApplicationException;
import com.fasttime.global.exception.ErrorCode;

public class PostDeletedException extends ApplicationException {

    private static final ErrorCode ERROR_CODE = ErrorCode.POST_NOT_FOUND;

    public PostDeletedException() {
        super(ERROR_CODE);
    }

    public PostDeletedException(String message) {
        super(ERROR_CODE, message);
    }
}
