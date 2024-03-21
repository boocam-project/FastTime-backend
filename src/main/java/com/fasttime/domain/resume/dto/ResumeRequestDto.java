package com.fasttime.domain.resume.dto;

import jakarta.validation.constraints.NotBlank;

public record ResumeRequestDto(
        @NotBlank String title,
        @NotBlank String content
) {

}
