package com.fasttime.domain.record.exception;

import com.fasttime.global.exception.ApplicationException;
import org.springframework.http.HttpStatus;

public class AlreadyExistsRecordException extends ApplicationException {

    public AlreadyExistsRecordException() {
        super(HttpStatus.BAD_REQUEST, "한 게시글에 좋아요와 싫어요를 모두 등록할 수는 없습니다.");
    }
}
