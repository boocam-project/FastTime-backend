package com.fasttime.domain.review.repository;

import com.fasttime.domain.review.entity.Review;
import com.fasttime.domain.review.entity.ReviewTag;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ReviewTagRepository extends JpaRepository<ReviewTag, Long> {

    void deleteByReview(Review review);

    @Query("SELECT COUNT(rt) FROM ReviewTag rt JOIN rt.review r WHERE r.bootcamp = :bootcamp")
    int countByBootcamp(String bootcamp);

    @Query("SELECT rt.tag.id, COUNT(rt) FROM ReviewTag rt JOIN rt.review r WHERE r.bootcamp = :bootcamp GROUP BY rt.tag.id")
    List<Object[]> countTagsByBootcampGroupedByTagId(String bootcamp);
}
