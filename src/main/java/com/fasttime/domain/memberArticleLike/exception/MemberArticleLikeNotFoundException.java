package com.fasttime.domain.memberArticleLike.exception;

import com.fasttime.global.exception.ApplicationException;
import com.fasttime.global.exception.ErrorCode;

public class MemberArticleLikeNotFoundException extends ApplicationException {

    private static final ErrorCode ERROR_CODE = ErrorCode.RECORD_NOT_FOUND;

    public MemberArticleLikeNotFoundException() {
        super(ERROR_CODE);
    }

    public MemberArticleLikeNotFoundException(String message) {
        super(ERROR_CODE, message);
    }
}
