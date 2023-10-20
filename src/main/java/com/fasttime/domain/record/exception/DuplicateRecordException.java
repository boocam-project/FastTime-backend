package com.fasttime.domain.record.exception;

import com.fasttime.global.exception.ApplicationException;
import com.fasttime.global.exception.ErrorCode;

public class DuplicateRecordException extends ApplicationException {

    private static final ErrorCode ERROR_CODE = ErrorCode.DUPLICATED_REQUEST_FOR_RECORD;

    public DuplicateRecordException() {
        super(ERROR_CODE);
    }

    public DuplicateRecordException(String message) {
        super(ERROR_CODE, message);
    }
}
