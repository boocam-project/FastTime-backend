package com.fasttime.domain.member.exception;

import com.fasttime.global.exception.ApplicationException;
import com.fasttime.global.exception.ErrorCode;

public class NicknameAlreadyExistsException extends ApplicationException {

    public NicknameAlreadyExistsException(ErrorCode errorCode) {

        super(errorCode);
    }
}
