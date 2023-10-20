package com.fasttime.domain.comment.exception;

import com.fasttime.global.exception.ApplicationException;
import com.fasttime.global.exception.ErrorCode;

public class CommentNotFoundException extends ApplicationException {

    private static final ErrorCode ERROR_CODE = ErrorCode.COMMENT_NOT_FOUND;

    public CommentNotFoundException() {
        super(ERROR_CODE);
    }

    public CommentNotFoundException(String message) {
        super(ERROR_CODE, message);
    }
}
