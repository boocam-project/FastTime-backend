package com.fasttime.domain.post.dto.service.request;

import java.time.LocalDateTime;
import lombok.Getter;

@Getter
public class PostDeleteServiceDto {

    private final Long postId;
    private final Long memberId;
    private final LocalDateTime deletedAt;

    public PostDeleteServiceDto(Long postId, Long memberId, LocalDateTime deletedAt) {
        this.postId = postId;
        this.memberId = memberId;
        this.deletedAt = deletedAt;
    }
}
