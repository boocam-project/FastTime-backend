package com.fasttime.domain.post.dto.service.request;

import lombok.Getter;

@Getter
public class PostCreateServiceDto {

    private final Long memberId;
    private final String title;
    private final String content;
    private final boolean anonymity;

    public PostCreateServiceDto(Long memberId, String title, String content, boolean anonymity) {
        this.memberId = memberId;
        this.title = title;
        this.content = content;
        this.anonymity = anonymity;
    }
}
