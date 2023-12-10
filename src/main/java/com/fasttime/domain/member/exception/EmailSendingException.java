package com.fasttime.domain.member.exception;

import com.fasttime.global.exception.ApplicationException;
import com.fasttime.global.exception.ErrorCode;

public class EmailSendingException extends ApplicationException {

    private static final ErrorCode ERROR_CODE = ErrorCode.EMAIL_SENDING_FAILURE;

    public EmailSendingException() {

        super(ERROR_CODE);
    }

}