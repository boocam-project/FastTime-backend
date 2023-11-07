package com.fasttime.global.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {

    // MEMBER
    MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 회원입니다."),
    MEMBER_ALREADY_REGISTERED(HttpStatus.BAD_REQUEST, "이미 가입된 회원입니다."),
    DUPLICATE_NICKNAME(HttpStatus.BAD_REQUEST, "이미 사용 중인 닉네임 입니다."),
    ACCOUNT_RECOVERY_SUCCESSFUL(HttpStatus.OK, "계정이 성공적으로 복구되었습니다!"),
    REGISTRATION_SUCCESS(HttpStatus.OK, "가입 성공!"),
    REGISTRATION_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "회원가입 실패"),
    MEMBER_UPDATE_SUCCESS(HttpStatus.OK, "회원 정보가 업데이트되었습니다."),

    // ADMIN
    ADMIN_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 관리자입니다"),

    // ARTICLE
    ARTICLE_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 게시글입니다."),
    BAD_REPORT_STATUS(HttpStatus.BAD_REQUEST, "신고 후처리를 할 수 없습니다."),
    REPORT_ACCEPTED_ARTICLE(HttpStatus.UNAUTHORIZED, "신고 처리된 게시글입니다."),
    HAS_NO_PERMISSION_WITH_THIS_ARTICLE(HttpStatus.UNAUTHORIZED, "해당 게시글에 대한 권한이 없습니다."),

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
