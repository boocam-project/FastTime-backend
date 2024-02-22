package com.fasttime.domain.reference.repository;

import com.fasttime.domain.reference.dto.request.ReferenceSearchRequestDto;
import com.fasttime.domain.reference.entity.Activity;
import java.util.List;

public interface ActivityCustomRepository {

    List<Activity> findAllBySearchConditions(ReferenceSearchRequestDto referenceSearchRequestDto);
}
