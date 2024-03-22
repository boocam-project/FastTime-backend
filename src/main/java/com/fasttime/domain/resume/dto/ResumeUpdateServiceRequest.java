package com.fasttime.domain.resume.dto;

public record ResumeUpdateServiceRequest(
        Long resumeId,
        Long memberId,
        String title,
        String content
) {

}
