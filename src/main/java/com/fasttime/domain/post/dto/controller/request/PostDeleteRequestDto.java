package com.fasttime.domain.post.dto.controller.request;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import javax.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class PostDeleteRequestDto {

    @NotNull
    private final Long postId;

    @NotNull
    private final Long memberId;

    @JsonCreator
    public PostDeleteRequestDto(
        @JsonProperty("postId") Long postId,
        @JsonProperty("memberId") Long memberId) {
        this.postId = postId;
        this.memberId = memberId;
    }
}
