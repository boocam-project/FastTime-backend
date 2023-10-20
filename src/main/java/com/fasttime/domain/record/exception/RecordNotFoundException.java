package com.fasttime.domain.record.exception;

import com.fasttime.global.exception.ApplicationException;
import com.fasttime.global.exception.ErrorCode;

public class RecordNotFoundException extends ApplicationException {

    private static final ErrorCode ERROR_CODE = ErrorCode.RECORD_NOT_FOUND;

    public RecordNotFoundException() {
        super(ERROR_CODE);
    }

    public RecordNotFoundException(String message) {
        super(ERROR_CODE, message);
    }
}
