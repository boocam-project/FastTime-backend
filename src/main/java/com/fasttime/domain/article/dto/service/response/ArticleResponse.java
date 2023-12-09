package com.fasttime.domain.article.dto.service.response;

import java.time.LocalDateTime;
import lombok.Builder;

@Builder
public record ArticleResponse(
    Long id,
    String title,
    String content,
    String nickname,
    boolean anonymity,
    int likeCount,
    int hateCount,
    LocalDateTime createdAt,
    LocalDateTime lastModifiedAt
) {

}
