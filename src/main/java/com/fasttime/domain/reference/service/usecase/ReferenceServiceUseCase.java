package com.fasttime.domain.reference.service.usecase;

import com.fasttime.domain.reference.dto.request.ReferenceSearchRequestDto;
import com.fasttime.domain.reference.dto.response.ActivityPageResponseDto;
import org.springframework.data.domain.Pageable;

public interface ReferenceServiceUseCase {

    ActivityPageResponseDto searchActivities(
        ReferenceSearchRequestDto searchRequestDto,
        Pageable pageable
    );
}
