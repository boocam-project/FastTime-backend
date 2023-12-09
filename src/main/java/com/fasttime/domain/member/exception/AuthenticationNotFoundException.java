package com.fasttime.domain.member.exception;

import com.fasttime.global.exception.ApplicationException;
import com.fasttime.global.exception.ErrorCode;

public class AuthenticationNotFoundException extends ApplicationException {
    public AuthenticationNotFoundException(){ super(ErrorCode.AUTHENTICATION_NOT_FOUND);}
}
