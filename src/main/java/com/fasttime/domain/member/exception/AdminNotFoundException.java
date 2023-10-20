package com.fasttime.domain.member.exception;

import com.fasttime.global.exception.ApplicationException;
import org.springframework.http.HttpStatus;

public class AdminNotFoundException extends ApplicationException {

    private static final HttpStatus status = HttpStatus.BAD_REQUEST;

    public AdminNotFoundException(String message) {
        super(status, message);
    }
}
