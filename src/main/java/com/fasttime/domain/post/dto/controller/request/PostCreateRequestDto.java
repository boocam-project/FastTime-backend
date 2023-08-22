package com.fasttime.domain.post.dto.controller.request;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class PostCreateRequestDto {

    @NotNull
    private final Long memberId;

    @NotBlank
    private final String title;

    @NotBlank
    private final String content;

    private final boolean anonymity;

    public PostCreateRequestDto(Long memberId, String title, String content, boolean anonymity) {
        this.memberId = memberId;
        this.title = title;
        this.content = content;
        this.anonymity = anonymity;
    }

    @Override
    public String toString() {
        return "PostCreateRequestDto{" +
            "memberId=" + memberId +
            ", title='" + title + '\'' +
            ", content='" + content + '\'' +
            ", anonymity=" + anonymity +
            '}';
    }
}
