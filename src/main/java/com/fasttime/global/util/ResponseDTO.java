package com.fasttime.global.util;

import lombok.Builder;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class ResponseDTO<T> {

    private final int code;
    private final String message;
    private final T data;

    @Builder
    private ResponseDTO(int code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public static ResponseDTO<Object> res(final HttpStatus httpStatus, final String message) {
        return ResponseDTO.<Object>builder()
            .code(httpStatus.value())
            .message(message)
            .build();
    }

    public static <T> ResponseDTO<T> res(final HttpStatus httpStatus, final T data) {
        return ResponseDTO.<T>builder()
            .code(httpStatus.value())
            .data(data)
            .build();
    }

    public static <T> ResponseDTO<T> res(final HttpStatus httpStatus,
        final String message,
        final T data) {
        return ResponseDTO.<T>builder()
            .code(httpStatus.value())
            .message(message)
            .data(data)
            .build();
    }
}
