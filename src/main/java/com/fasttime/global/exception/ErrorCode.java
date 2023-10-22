package com.fasttime.global.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {

    // MEMBER
    MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 회원입니다."),

    // ADMIN
    ADMIN_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 관리자입니다"),

    // POST
    POST_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 게시글입니다."),
    POST_IS_REPORTED(HttpStatus.UNAUTHORIZED, "해당 게시글은 수정할 수 있는 상태가 아닙니다."),
    HAS_NO_PERMISSION_WITH_THIS_POST(HttpStatus.UNAUTHORIZED, "해당 게시글에 대한 권한이 없습니다."),

    // COMMENT
    COMMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 댓글입니다."),

    // RECORD
    RECORD_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 좋아요/싫어요 입니다."),
    CANNOT_RECORD_BOTH_IN_ONE_POST(HttpStatus.BAD_REQUEST, "한 게시글에 좋아요와 싫어요를 모두 등록할 수는 없습니다."),
    DUPLICATED_REQUEST_FOR_RECORD(HttpStatus.BAD_REQUEST, "중복된 좋아요/싫어요 등록 요청입니다."),

    // REPORT
    ALREADY_REPORTED_THIS_POST(HttpStatus.BAD_REQUEST, "이미 신고한 게시글입니다."),

    // 5xx
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 내부 에러");

    private HttpStatus httpStatus;
    private String message;

    ErrorCode(HttpStatus httpStatus, String message) {
        this.httpStatus = httpStatus;
        this.message = message;
    }
}