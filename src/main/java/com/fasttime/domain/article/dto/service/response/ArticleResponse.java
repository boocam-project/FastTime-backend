package com.fasttime.domain.article.dto.service.response;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
public class ArticleResponse {

    private final Long id;
    private final String title;
    private final String content;
    private final String nickname;
    private final boolean anonymity;
    private final int likeCount;
    private final int hateCount;
    private final LocalDateTime createdAt;
    private final LocalDateTime lastModifiedAt;

    @Builder
    private ArticleResponse(Long id, String title, String content, String nickname, boolean anonymity,
        int likeCount, int hateCount, LocalDateTime createdAt, LocalDateTime lastModifiedAt) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.nickname = nickname;
        this.anonymity = anonymity;
        this.likeCount = likeCount;
        this.hateCount = hateCount;
        this.createdAt = createdAt;
        this.lastModifiedAt = lastModifiedAt;
    }
}
