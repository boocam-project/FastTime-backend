package com.fasttime.domain.review.repository;

import com.fasttime.domain.review.entity.Review;
import com.fasttime.domain.review.entity.ReviewTag;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ReviewTagRepository extends JpaRepository<ReviewTag, Long> {

    void deleteByReview(Review review);

    @Query("SELECT rt.tag.id, COUNT(rt) FROM ReviewTag rt JOIN rt.review r WHERE r.bootCamp.name = :bootcampName GROUP BY rt.tag.id")
    List<Object[]> countTagsByBootcampGroupedByTagId(@Param("bootcampName") String bootcampName);
}
