package com.fasttime.domain.report.exception;

import com.fasttime.global.exception.ApplicationException;
import org.springframework.http.HttpStatus;

public class AlreadyDeletedPostException extends ApplicationException {

    public AlreadyDeletedPostException(String message) {
        super(HttpStatus.BAD_REQUEST, message);
    }
}
