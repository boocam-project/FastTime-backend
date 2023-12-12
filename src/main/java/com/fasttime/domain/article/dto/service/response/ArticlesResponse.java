package com.fasttime.domain.article.dto.service.response;

import java.time.LocalDateTime;
import lombok.Builder;

@Builder
public record ArticlesResponse(
    Long id,
    String title,
    String nickname,
    boolean isAnonymity,
    int commentCounts,
    int likeCount,
    int hateCount,
    LocalDateTime createdAt,
    LocalDateTime lastModifiedAt
) {

}

