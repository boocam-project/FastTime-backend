package com.fasttime.domain.reference.repository;

import com.fasttime.domain.reference.entity.Competition;
import com.fasttime.domain.reference.entity.RecruitmentStatus;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CompetitionRepository extends JpaRepository<Competition, Long>,
    CompetitionCustomRepository {

    List<Competition> findAllByStatus(RecruitmentStatus status);

    Boolean existsByTitle(String title);
}
