package com.fasttime.domain.review.repository;

import com.fasttime.domain.review.entity.Review;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    List<Review> findByMemberId(Long memberId);

    @Query("SELECT DISTINCT r.bootCamp.name FROM Review r")
    List<String> findAllBootcamps();

    @Query("SELECT COUNT(r) FROM Review r WHERE r.bootCamp.name = :bootcampName")
    int countByBootcamp(@Param("bootcampName") String bootcampName);

    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.bootCamp.name = :bootcampName")
    double findAverageRatingByBootcamp(@Param("bootcampName") String bootcampName);

    @Query("SELECT r FROM Review r WHERE r.bootCamp.name = :bootcampName AND r.deletedAt IS NULL")
    Page<Review> findByBootcampName(@Param("bootcampName") String bootcampName, Pageable pageable);

    @Modifying
    @Query("DELETE FROM Review r WHERE r.deletedAt IS NOT NULL AND r.deletedAt <= :cutoffDate")
    void deleteReviewsOlderThan7Days(@Param("cutoffDate") LocalDateTime cutoffDate);

    @Query("SELECT r FROM Review r WHERE r.deletedAt IS NULL")
    Page<Review> findAll(Pageable pageable);
}
