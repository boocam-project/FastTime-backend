package com.fasttime.domain.memberArticleLike.exception;

import com.fasttime.global.exception.ApplicationException;
import com.fasttime.global.exception.ErrorCode;

public class AlreadyExistsMemberArticleLikeException extends ApplicationException {

    private static final ErrorCode ERROR_CODE = ErrorCode.CANNOT_RECORD_BOTH_IN_ONE_POST;

    public AlreadyExistsMemberArticleLikeException() {
        super(ERROR_CODE);
    }

    public AlreadyExistsMemberArticleLikeException(String message) {
        super(ERROR_CODE, message);
    }
}
