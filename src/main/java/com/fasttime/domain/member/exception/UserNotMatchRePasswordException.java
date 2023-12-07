package com.fasttime.domain.member.exception;

import com.fasttime.global.exception.ApplicationException;
import com.fasttime.global.exception.ErrorCode;

public class UserNotMatchRePasswordException extends ApplicationException {

    private static final ErrorCode ERROR_CODE = ErrorCode.MEMBER_NOT_MATCH_RE_PASSWORD;

    public UserNotMatchRePasswordException(){super(ERROR_CODE);}
}
