package com.fasttime.domain.resume.exception;

import com.fasttime.global.exception.ApplicationException;
import com.fasttime.global.exception.ErrorCode;

public class NoResumeWriterException extends ApplicationException {

    private static final ErrorCode ERROR_CODE = ErrorCode.HAS_NO_PERMISSION_WITH_THIS_RESUME;

    public NoResumeWriterException() {
        super(ERROR_CODE);
    }

    public NoResumeWriterException(Long resumeId) {
        super(ERROR_CODE, String.format("Resume Not Found: %d", resumeId));
    }

    public NoResumeWriterException(String message) {
        super(ERROR_CODE, message);
    }
}
