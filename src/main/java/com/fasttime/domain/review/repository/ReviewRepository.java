package com.fasttime.domain.review.repository;

import com.fasttime.domain.review.entity.Review;
import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    Review findByMemberId(Long memberId);

    @Query("SELECT DISTINCT r.bootcamp FROM Review r")
    List<String> findAllBootcamps();

    @Query("SELECT COUNT(r) FROM Review r WHERE r.bootcamp = :bootcamp")
    int countByBootcamp(String bootcamp);

    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.bootcamp = :bootcamp")
    double findAverageRatingByBootcamp(String bootcamp);

    List<Review> findByBootcamp(String bootcamp, Sort sort);
}
