package com.fasttime.domain.member.exception;

import com.fasttime.global.exception.ApplicationException;
import com.fasttime.global.exception.ErrorCode;

public class UnmatchedMemberException extends ApplicationException {

    public UnmatchedMemberException() {
        super(ErrorCode.UNMATCHED_MEMBER);
    }
}
