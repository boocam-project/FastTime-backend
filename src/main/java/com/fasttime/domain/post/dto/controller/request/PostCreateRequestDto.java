package com.fasttime.domain.post.dto.controller.request;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.Getter;

@Getter
public class PostCreateRequestDto {

    @NotNull
    private final Long memberId;

    @NotBlank
    @Size(min = 5, message = "제목은 5자리 이상이어야 합니다.")
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
