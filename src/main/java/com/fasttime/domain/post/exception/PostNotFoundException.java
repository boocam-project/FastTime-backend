package com.fasttime.domain.post.exception;

import com.fasttime.global.exception.ApplicationException;
import org.springframework.http.HttpStatus;

public class PostNotFoundException extends ApplicationException {

    private static final HttpStatus httpStatus = HttpStatus.NOT_FOUND;

    public PostNotFoundException() {
        super(httpStatus, "존재하지 않는 게시글입니다.");
    }
}
