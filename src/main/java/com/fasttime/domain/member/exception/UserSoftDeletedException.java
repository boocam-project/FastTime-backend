package com.fasttime.domain.member.exception;

import com.fasttime.global.exception.ApplicationException;
import com.fasttime.global.exception.ErrorCode;

public class UserSoftDeletedException extends ApplicationException {

    private static final ErrorCode ERROR_CODE = ErrorCode.MEMBER_SOFT_DELETED;

    public UserSoftDeletedException(){super(ERROR_CODE);}

}
