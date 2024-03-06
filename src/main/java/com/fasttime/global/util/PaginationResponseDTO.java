package com.fasttime.global.util;

public record PaginationResponseDTO(
    int currentPage,
    int totalPages,
    int currentElements,
    long totalElements
) {}
