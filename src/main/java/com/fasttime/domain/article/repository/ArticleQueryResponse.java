package com.fasttime.domain.article.repository;

import java.time.LocalDateTime;
import lombok.Getter;

@Getter
public class ArticleQueryResponse {

    Long id;
    Long memberId;
    String nickname;
    String title;
    String content;
    boolean isAnonymity;
    int commentCount;
    int likeCount;
    int hateCount;
    LocalDateTime createdAt;
    LocalDateTime lastModifiedAt;
    LocalDateTime deletedAt;
}
