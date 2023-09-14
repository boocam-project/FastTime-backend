package com.fasttime.domain.post.repository;

import com.fasttime.domain.post.entity.Post;

import com.fasttime.domain.post.entity.ReportStatus;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Long> {

    List<Post> findAllByReportStatus(ReportStatus reportStatus);

}
