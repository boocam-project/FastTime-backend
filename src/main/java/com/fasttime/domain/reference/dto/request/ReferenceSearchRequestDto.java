package com.fasttime.domain.reference.dto.request;

import lombok.Builder;
import org.springframework.data.domain.PageRequest;

public record ReferenceSearchRequestDto(
    String keyword,
    boolean before,
    boolean during,
    boolean closed,
    String orderBy,
    int page,
    int pageSize
) {

    @Builder
    public ReferenceSearchRequestDto(
        String keyword,
        boolean before,
        boolean during,
        boolean closed,
        String orderBy,
        int page,
        int pageSize
    ) {
        this.keyword = keyword;
        this.before = before;
        this.during = during;
        this.closed = closed;
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
