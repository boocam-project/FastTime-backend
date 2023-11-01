package com.fasttime.domain.article.dto.service.response;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
public class ArticlesResponse {

    private final Long id;
    private final String title;
    private final String nickname;
    private final boolean anonymity;
    private final int commentCounts;
    private final int likeCount;
    private final int hateCount;
    private final LocalDateTime createdAt;
    private final LocalDateTime lastModifiedAt;

    @Builder
    private ArticlesResponse(Long id, String title, String nickname, boolean anonymity, int commentCounts, int likeCount,
        int hateCount, LocalDateTime createdAt, LocalDateTime lastModifiedAt) {
        this.id = id;
        this.title = title;
        this.nickname = nickname;
        this.anonymity = anonymity;
        this.commentCounts = commentCounts;
        this.likeCount = likeCount;
        this.hateCount = hateCount;
        this.createdAt = createdAt;
        this.lastModifiedAt = lastModifiedAt;
    }
}
