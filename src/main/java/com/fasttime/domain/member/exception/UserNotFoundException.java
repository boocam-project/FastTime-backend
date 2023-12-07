package com.fasttime.domain.member.exception;

import com.fasttime.global.exception.ApplicationException;

import com.fasttime.global.exception.ErrorCode;
import org.springframework.http.HttpStatus;

public class UserNotFoundException extends ApplicationException {


    private static final ErrorCode errorCode = ErrorCode.MEMBER_NOT_FOUND;
    public UserNotFoundException(String message) {
        super(errorCode, message);
    }
}

