package com.fasttime.domain.member.exception;

import com.fasttime.global.exception.ApplicationException;
import com.fasttime.global.exception.ErrorCode;

public class MemberNotFoundException extends ApplicationException {


    private static final ErrorCode errorCode = ErrorCode.MEMBER_NOT_FOUND;

    public MemberNotFoundException() {
        super(errorCode);
    }

    public MemberNotFoundException(String message) {
        super(errorCode, message);
    }
}

