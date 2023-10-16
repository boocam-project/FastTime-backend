package com.fasttime.domain.record.dto;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class RecordDTO {

    private Long id;
    private Long memberId;
    private Long postId;
    private Boolean isLike;
}
