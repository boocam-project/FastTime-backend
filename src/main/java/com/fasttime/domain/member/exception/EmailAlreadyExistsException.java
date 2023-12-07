package com.fasttime.domain.member.exception;

import com.fasttime.global.exception.ApplicationException;
import com.fasttime.global.exception.ErrorCode;

public class EmailAlreadyExistsException extends ApplicationException {

    public EmailAlreadyExistsException(ErrorCode errorCode) {

        super(errorCode);
    }
}
