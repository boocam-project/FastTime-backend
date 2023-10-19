package com.fasttime.domain.post.exception;

import com.fasttime.global.exception.ApplicationException;
import org.springframework.http.HttpStatus;

public class PostReportedException extends ApplicationException {

    public PostReportedException() {
        super(HttpStatus.FORBIDDEN, "수정할 수 없는 상태입니다.");
    }
}
