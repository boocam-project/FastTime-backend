package com.fasttime.domain.review.repository;

import com.fasttime.domain.review.dto.response.BootcampReviewSummaryDTO;
import com.fasttime.domain.review.entity.Review;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ReviewCustomRepository {

    List<String> findAllBootcamps();

    Page<BootcampReviewSummaryDTO> findBootcampReviewSummaries(Pageable pageable);

    int countByBootcamp(String bootcampName);

    double findAverageRatingByBootcamp(String bootcampName);

    Page<Review> findByBootcampName(String bootcampName, Pageable pageable);

    void deleteReviewsOlderThan7Days(LocalDateTime cutoffDate);

    Page<Review> findAllReviews(Pageable pageable);
}
