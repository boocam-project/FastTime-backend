package com.fasttime.domain.post.dto.usecase.request;

import lombok.Getter;

@Getter
public class PostCreateServiceDto {

    private final Long memberId;
    private final String title;
    private final String content;
    private final boolean anounumity;

    public PostCreateServiceDto(Long memberId, String title, String content, boolean anounumity) {
        this.memberId = memberId;
        this.title = title;
        this.content = content;
        this.anounumity = anounumity;
    }
}
