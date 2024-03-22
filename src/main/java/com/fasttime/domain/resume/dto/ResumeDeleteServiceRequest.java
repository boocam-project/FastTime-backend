package com.fasttime.domain.resume.dto;

public record ResumeDeleteServiceRequest(
        Long resumeId,
        Long requestUserId
) {

}
