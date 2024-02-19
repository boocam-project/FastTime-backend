package com.fasttime.domain.review.repository;

import com.fasttime.domain.review.entity.Review;
import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    Review findByMemberId(Long memberId);

    List<Review> findByBootcamp(String bootcamp, Sort sort);
}
