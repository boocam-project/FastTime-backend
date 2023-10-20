package com.fasttime.domain.post.exception;

import com.fasttime.global.exception.ApplicationException;
import com.fasttime.global.exception.ErrorCode;

public class NotPostWriterException extends ApplicationException {

    private static final ErrorCode ERROR_CODE = ErrorCode.HAS_NO_PERMISSION_WITH_THIS_POST;

    public NotPostWriterException() {
        super(ERROR_CODE);
    }

    public NotPostWriterException(String message) {
        super(ERROR_CODE, message);
    }
}
