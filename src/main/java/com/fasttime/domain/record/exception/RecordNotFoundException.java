package com.fasttime.domain.record.exception;

import com.fasttime.global.exception.ApplicationException;
import org.springframework.http.HttpStatus;

public class RecordNotFoundException extends ApplicationException {

    private static final HttpStatus httpStatus = HttpStatus.NOT_FOUND;

    public RecordNotFoundException() {
        super(httpStatus, "존재하지 않는 좋아요/싫어요 입니다.");
    }
}
