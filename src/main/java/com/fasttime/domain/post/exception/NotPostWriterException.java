package com.fasttime.domain.post.exception;

import com.fasttime.global.exception.ApplicationException;
import org.springframework.http.HttpStatus;

public class NotPostWriterException extends ApplicationException {

    private static final HttpStatus httpStatus = HttpStatus.BAD_REQUEST;

    public NotPostWriterException() {
        super(httpStatus, "해당 게시글에 대한 권한이 없습니다.");
    }
}
