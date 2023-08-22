package com.fasttime.global.util;

import lombok.Builder;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class ResponseDTO {

    private final int code;
    private final String message;
    private final Object data;

    @Builder
    private ResponseDTO(int code, String message, Object data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public static ResponseDTO res(final HttpStatus httpStatus, final String message) {
        return ResponseDTO.builder().code(httpStatus.value()).message(message).build();
    }

    public static ResponseDTO res(final HttpStatus httpStatus, final String message,
        final Object object) {
        return ResponseDTO.builder().code(httpStatus.value()).message(message).data(object).build();
    }
}
