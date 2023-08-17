package com.fasttime.domain.post.entity;

import lombok.Getter;

@Getter
public enum ReportStatus {

    NORMAL("일반 게시글"),
    REPORTED("검토 중"),
    REPORTE_ABORTED("검토 완료. 이상 없음");

    private final String value;

    ReportStatus(String value) {
        this.value = value;
    }
}
