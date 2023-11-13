package com.fasttime.domain.comment.exception;

import com.fasttime.global.exception.ApplicationException;
import com.fasttime.global.exception.ErrorCode;

public class MultipleSearchConditionException extends ApplicationException {

    private static final ErrorCode ERROR_CODE = ErrorCode.CANNOT_MULTIPLE_SEARCH_CONDITION;

    public MultipleSearchConditionException() {
        super(ERROR_CODE);
    }
}
