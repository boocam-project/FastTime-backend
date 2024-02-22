package com.fasttime.domain.reference.dto.request;

import lombok.Builder;
import org.springframework.data.domain.PageRequest;

public record ReferencePageRequestDto(
    String orderBy,
    int page,
    int pageSize
) {

    @Builder
    public ReferencePageRequestDto(
        String orderBy,
        int page,
        int pageSize
    ) {
        this.orderBy = orderBy;
        this.page = Math.max(page, 0);
        int DEFAULT_SIZE = 10;
        int MAX_SIZE = 50;
        this.pageSize = pageSize > MAX_SIZE ? DEFAULT_SIZE : pageSize;
    }

    public PageRequest of() {
        return PageRequest.of(page, pageSize);
    }
}
