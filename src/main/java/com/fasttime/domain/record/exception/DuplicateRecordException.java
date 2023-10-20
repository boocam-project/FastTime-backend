package com.fasttime.domain.record.exception;

import com.fasttime.global.exception.ApplicationException;
import org.springframework.http.HttpStatus;

public class DuplicateRecordException extends ApplicationException {

    public DuplicateRecordException() {
        super(HttpStatus.BAD_REQUEST, "중복된 좋아요/싫어요 등록 요청입니다.");
    }
}
