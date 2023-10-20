package com.fasttime.domain.member.exception;

import com.fasttime.global.exception.ApplicationException;
import com.fasttime.global.exception.ErrorCode;

public class AdminNotFoundException extends ApplicationException {

    private static final ErrorCode ERROR_CODE = ErrorCode.ADMIN_NOT_FOUND;


    public AdminNotFoundException() {
        super(ERROR_CODE);
    }

    public AdminNotFoundException(String message) {
        super(ERROR_CODE, message);
    }
}
