package com.fasttime.domain.reference.exception;

import com.fasttime.global.exception.ApplicationException;
import com.fasttime.global.exception.ErrorCode;

public class CompetitionNotFountException extends ApplicationException {

    private static final ErrorCode ERROR_CODE = ErrorCode.COMPETITION_NOT_FOUND;

    public CompetitionNotFountException() {
        super(ERROR_CODE);
    }
}
