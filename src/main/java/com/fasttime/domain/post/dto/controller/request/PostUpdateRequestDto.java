package com.fasttime.domain.post.dto.controller.request;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class PostUpdateRequestDto {

    @NotNull
    private final Long postId;

    @NotBlank
    private final String content;

    public PostUpdateRequestDto(Long postId, String content) {
        this.postId = postId;
        this.content = content;
    }
}
