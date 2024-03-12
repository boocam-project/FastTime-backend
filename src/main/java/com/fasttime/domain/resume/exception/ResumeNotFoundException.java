package com.fasttime.domain.resume.exception;


import com.fasttime.global.exception.ApplicationException;
import com.fasttime.global.exception.ErrorCode;

public class ResumeNotFoundException extends ApplicationException {

    private static final ErrorCode ERROR_CODE = ErrorCode.RESUME_NOT_FOUND;

    public ResumeNotFoundException() {
        super(ERROR_CODE);
    }

    public ResumeNotFoundException(Long resumeId) {
        super(ERROR_CODE, String.format("Resume Not Found: %d", resumeId));
    }

    public ResumeNotFoundException(String message) {
        super(ERROR_CODE, message);
    }
}
