package com.fasttime.domain.report.repository;

import com.fasttime.domain.post.entity.Post;
import com.fasttime.domain.report.entity.Report;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReportRepository extends JpaRepository<Report, Long> {

    Optional<List<Report>> findAllByPost(Post post);
}
