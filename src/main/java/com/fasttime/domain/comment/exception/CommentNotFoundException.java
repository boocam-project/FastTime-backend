package com.fasttime.domain.comment.exception;

import com.fasttime.global.exception.ApplicationException;
import org.springframework.http.HttpStatus;

public class CommentNotFoundException extends ApplicationException {

    private static final HttpStatus httpStatus = HttpStatus.NOT_FOUND;

    public CommentNotFoundException() {
        super(httpStatus, "존재하지 않는 댓글입니다.");
    }
}
