package com.fasttime.domain.resume.dto;

import java.time.LocalDateTime;

public record ResumeDeleteServiceRequest(
        Long resumeId,
        Long requestUserId,
        LocalDateTime deleteAt

) {

}
