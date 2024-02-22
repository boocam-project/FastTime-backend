package com.fasttime.domain.reference.repository;

import com.fasttime.domain.reference.entity.Activity;
import com.fasttime.domain.reference.entity.RecruitmentStatus;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ActivityRepository extends JpaRepository<Activity, Long>, ActivityCustomRepository {

    List<Activity> findAllByStatus(RecruitmentStatus status);

    Boolean existsByTitle(String title);
}
