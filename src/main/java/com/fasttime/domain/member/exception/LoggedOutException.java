package com.fasttime.domain.member.exception;


import com.fasttime.global.exception.ApplicationException;
import com.fasttime.global.exception.ErrorCode;

public class LoggedOutException extends ApplicationException {

    public LoggedOutException() {
        super(ErrorCode.LOGGED_OUT);
    }
}
