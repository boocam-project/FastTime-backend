package com.fasttime.domain.post.entity;

import javax.persistence.Embeddable;
import javax.persistence.Lob;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Embeddable
public class PostContent {

    @Lob
    private String content;

    public PostContent(String content) {
        this.content = content;
    }

    public void updateContent(String content) {
        this.content = content;
    }
}
