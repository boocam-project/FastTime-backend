package com.fasttime.domain.review.exception;

import com.fasttime.global.exception.ApplicationException;
import com.fasttime.global.exception.ErrorCode;

public class BootCampNotFoundException extends ApplicationException {

    private static final ErrorCode ERROR_CODE = ErrorCode.BOOTCAMP_NOT_FOUND;

    public BootCampNotFoundException() {
        super(ERROR_CODE);
    }
}
