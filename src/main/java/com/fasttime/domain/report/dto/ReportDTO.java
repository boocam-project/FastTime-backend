package com.fasttime.domain.report.dto;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class ReportDTO {

    private Long id;
    private Long memberId;
    private Long postId;
}
