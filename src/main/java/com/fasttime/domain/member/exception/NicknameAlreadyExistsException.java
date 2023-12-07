package com.fasttime.domain.member.exception;

import com.fasttime.global.exception.ApplicationException;
import com.fasttime.global.exception.ErrorCode;

public class NicknameAlreadyExistsException extends ApplicationException {

    private static final ErrorCode errorCode = ErrorCode.DUPLICATE_NICKNAME;

    public NicknameAlreadyExistsException() {

        super(errorCode);
    }
}
