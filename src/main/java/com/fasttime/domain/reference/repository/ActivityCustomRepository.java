package com.fasttime.domain.reference.repository;

import com.fasttime.domain.reference.dto.request.ReferenceSearchRequestDto;
import com.fasttime.domain.reference.entity.Activity;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ActivityCustomRepository {

    Page<Activity> findAllBySearchConditions(
        ReferenceSearchRequestDto referenceSearchRequestDto,
        Pageable pageable
    );

    List<Activity> findAllByRecruitmentStart(LocalDate now);
}
