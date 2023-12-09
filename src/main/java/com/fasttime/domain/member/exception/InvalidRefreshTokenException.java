package com.fasttime.domain.member.exception;


import com.fasttime.global.exception.ApplicationException;
import com.fasttime.global.exception.ErrorCode;

public class InvalidRefreshTokenException extends ApplicationException {

    public InvalidRefreshTokenException() {
        super(ErrorCode.INVALID_REFRESH_TOKEN);
    }
}