package com.fasttime.domain.article.entity;

import javax.persistence.Embeddable;
import javax.persistence.Lob;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Embeddable
class ArticleContent {

    @Lob
    private String content;

    public ArticleContent(String content) {
        this.content = content;
    }

    public void updateContent(String content) {
        this.content = content;
    }
}
