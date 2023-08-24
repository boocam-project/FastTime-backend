package com.fasttime.domain.report.exception;

public class AlreadyDeletedPostException extends RuntimeException {

    public AlreadyDeletedPostException(String message) {
        super(message);
    }
}
