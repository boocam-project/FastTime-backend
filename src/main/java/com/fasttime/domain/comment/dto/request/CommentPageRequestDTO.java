package com.fasttime.domain.comment.dto.request;

import lombok.Builder;

public final class CommentPageRequestDTO {

    private final int page;
    private final int size;

    @Builder
    public CommentPageRequestDTO(int page, int size) {
        this.page = Math.max(page, 0);
        int DEFAULT_SIZE = 10;
        int MAX_SIZE = 50;
        this.size = size > MAX_SIZE ? DEFAULT_SIZE : size;
    }

    public org.springframework.data.domain.PageRequest of() {
        return org.springframework.data.domain.PageRequest.of(page, size);
    }
}
