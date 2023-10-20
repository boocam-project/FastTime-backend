package com.fasttime.domain.record.exception;

import com.fasttime.global.exception.ApplicationException;
import com.fasttime.global.exception.ErrorCode;

public class AlreadyExistsRecordException extends ApplicationException {

    private static final ErrorCode ERROR_CODE = ErrorCode.CANNOT_RECORD_BOTH_IN_ONE_POST;

    public AlreadyExistsRecordException() {
        super(ERROR_CODE);
    }

    public AlreadyExistsRecordException(String message) {
        super(ERROR_CODE, message);
    }
}
