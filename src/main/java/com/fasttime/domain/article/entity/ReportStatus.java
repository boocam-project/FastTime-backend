package com.fasttime.domain.article.entity;

import lombok.Getter;

@Getter
public enum ReportStatus {

    NORMAL("일반 게시글"),
    REPORTED("검토 중"),
    REPORT_REJECTED("검토 완료. 이상 없음");

    private final String value;

    ReportStatus(String value) {
        this.value = value;
    }
}
