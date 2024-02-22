package com.fasttime.domain.reference.dto.request;

import lombok.Builder;

@Builder
public record ReferenceSearchRequestDto(
    String keyword,
    boolean before,
    boolean during,
    boolean closed
) {

}
