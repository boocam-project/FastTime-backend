package com.fasttime.domain.post.exception;

import com.fasttime.global.exception.ApplicationException;
import org.springframework.http.HttpStatus;

public class PostDeletedException extends ApplicationException {

    public PostDeletedException() {
        super(HttpStatus.NOT_FOUND, "삭제된 게시글입니다.");
    }
}
