package com.fasttime.domain.memberArticleLike.exception;

import com.fasttime.global.exception.ApplicationException;
import com.fasttime.global.exception.ErrorCode;

public class DuplicateMemberArticleLikeException extends ApplicationException {

    private static final ErrorCode ERROR_CODE = ErrorCode.DUPLICATED_REQUEST_FOR_RECORD;

    public DuplicateMemberArticleLikeException() {
        super(ERROR_CODE);
    }

    public DuplicateMemberArticleLikeException(String message) {
        super(ERROR_CODE, message);
    }
}
