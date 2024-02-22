package com.fasttime.domain.reference.dto.response;

import com.fasttime.domain.reference.entity.Activity;
import lombok.Builder;

@Builder
public record ReferenceResponseDto(
    long id,
    String title,
    String organization,
    long dDay,
    String imageUrl
) {

    public static ReferenceResponseDto of(Activity activity) {
        return ReferenceResponseDto.builder()
            .build();
    }
}
