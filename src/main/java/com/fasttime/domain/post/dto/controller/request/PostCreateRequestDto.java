package com.fasttime.domain.post.dto.controller.request;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import javax.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class PostCreateRequestDto {

    @NotBlank
    private final String title;

    @NotBlank
    private final String content;

    private final boolean anonymity;

    @JsonCreator
    public PostCreateRequestDto(
        @JsonProperty(value = "title") String title,
        @JsonProperty(value = "content") String content,
        @JsonProperty(value = "anonymity") boolean anonymity) {

        this.title = title;
        this.content = content;
        this.anonymity = anonymity;
    }
}
