package com.fasttime.domain.member.exception;

import com.fasttime.global.exception.ApplicationException;
import com.fasttime.global.exception.ErrorCode;

public class EmailAlreadyExistsException extends ApplicationException {

    private static final ErrorCode ERROR_CODE = ErrorCode.MEMBER_ALREADY_REGISTERED;

    public EmailAlreadyExistsException() {

        super(ERROR_CODE);
    }
}
