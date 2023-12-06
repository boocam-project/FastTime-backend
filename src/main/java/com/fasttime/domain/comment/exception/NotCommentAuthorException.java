package com.fasttime.domain.comment.exception;

import com.fasttime.global.exception.ApplicationException;
import com.fasttime.global.exception.ErrorCode;

public class NotCommentAuthorException extends ApplicationException {

    private static final ErrorCode ERROR_CODE = ErrorCode.HAS_NO_PERMISSION_WITH_THIS_COMMENT;

    public NotCommentAuthorException() {
        super(ERROR_CODE);
    }
}
