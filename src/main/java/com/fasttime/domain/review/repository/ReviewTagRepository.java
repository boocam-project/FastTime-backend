package com.fasttime.domain.review.repository;

import com.fasttime.domain.review.entity.Review;
import com.fasttime.domain.review.entity.ReviewTag;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewTagRepository extends JpaRepository<ReviewTag, Long>,
    ReviewTagCustomRepository {

    void deleteByReview(Review review);
}
