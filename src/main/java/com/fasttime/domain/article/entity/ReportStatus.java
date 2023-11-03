package com.fasttime.domain.article.entity;

import lombok.Getter;

@Getter
public enum ReportStatus {

    NORMAL("일반 게시글"),
    WAIT_FOR_REPORT_REVIEW("검토 중"),
    REPORT_ACCEPT("검토 완료. 이상 있음"),
    REPORT_REJECT("검토 완료. 이상 없음");

    private final String value;

    ReportStatus(String value) {
        this.value = value;
    }
}
