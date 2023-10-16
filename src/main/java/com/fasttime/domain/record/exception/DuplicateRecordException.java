package com.fasttime.domain.record.exception;

import com.fasttime.global.exception.ApplicationException;
import org.springframework.http.HttpStatus;

public class DuplicateRecordException extends ApplicationException {

    public DuplicateRecordException(String message) {
        super(HttpStatus.BAD_REQUEST, message);
    }
}
