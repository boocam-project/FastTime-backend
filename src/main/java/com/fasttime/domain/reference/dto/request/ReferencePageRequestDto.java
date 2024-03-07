package com.fasttime.domain.reference.dto.request;

import lombok.Builder;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;

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

    public PageRequest toPageable() {
        return switch (orderBy) {
            case "d-day" -> PageRequest.of(page, pageSize, Sort.by(Direction.ASC, "endDate"));
            case "latest" -> PageRequest.of(page, pageSize, Sort.by(Direction.DESC, "id"));
            default -> PageRequest.of(page, pageSize);
        };
    }
}
