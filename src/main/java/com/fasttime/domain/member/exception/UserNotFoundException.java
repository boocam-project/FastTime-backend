package com.fasttime.domain.member.exception;

import com.fasttime.global.exception.ApplicationException;

import org.springframework.http.HttpStatus;

public class UserNotFoundException extends ApplicationException {

    private static final HttpStatus status = HttpStatus.BAD_REQUEST;
    public UserNotFoundException(String message) {
        super(status,message);
    }
}
