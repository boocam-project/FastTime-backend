package com.fasttime.domain.article.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Lob;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Embeddable
class ArticleContent {

    @Lob
    @Column(columnDefinition = "LONGTEXT")
    private String content;

    public ArticleContent(String content) {
        this.content = content;
    }

    public void updateContent(String content) {
        this.content = content;
    }
}