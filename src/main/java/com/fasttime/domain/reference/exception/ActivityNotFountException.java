package com.fasttime.domain.reference.exception;

import com.fasttime.global.exception.ApplicationException;
import com.fasttime.global.exception.ErrorCode;

public class ActivityNotFountException extends ApplicationException {

    private static final ErrorCode ERROR_CODE = ErrorCode.ACTIVITY_NOT_FOUND;

    public ActivityNotFountException() {
        super(ERROR_CODE);
    }
}
