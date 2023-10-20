package com.fasttime.domain.report.exception;

import com.fasttime.global.exception.ApplicationException;
import org.springframework.http.HttpStatus;

public class AlreadyDeletedPostException extends ApplicationException {

    public AlreadyDeletedPostException() {
        super(HttpStatus.BAD_REQUEST, "이미 삭제된 게시글입니다.");
    }
}
