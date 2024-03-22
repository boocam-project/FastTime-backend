package com.fasttime.domain.resume.dto;

import lombok.Builder;

@Builder
public record ResumeResponseDto(
        Long id,
        String title,
        String content,
        String writer,
        int likeCount,
        int viewCount
) {

}
