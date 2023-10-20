package com.fasttime.domain.member.exception;

import com.fasttime.global.exception.ApplicationException;
import com.fasttime.global.exception.ErrorCode;

public class UserNotFoundException extends ApplicationException {

    private static final ErrorCode ERROR_CODE = ErrorCode.MEMBER_NOT_FOUND;

    public UserNotFoundException() {
        super(ERROR_CODE);
    }

    public UserNotFoundException(String message) {
        super(ERROR_CODE, message);
    }
}
