package com.fasttime.domain.review.controller;

import com.fasttime.domain.review.dto.request.ReviewRequestDTO;
import com.fasttime.domain.review.dto.response.ReviewResponseDTO;
import com.fasttime.domain.review.entity.Review;
import com.fasttime.domain.review.service.ReviewService;
import com.fasttime.global.util.ResponseDTO;
import com.fasttime.global.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;
    private final SecurityUtil securityUtil;

    @PostMapping("api/v2/reviews")
    public ResponseEntity<ResponseDTO<ReviewResponseDTO>> createReview(@RequestBody ReviewRequestDTO requestDTO) {
        Long memberId = securityUtil.getCurrentMemberId();
        Review review = reviewService.createReview(requestDTO, memberId);

        ReviewResponseDTO responseDTO = ReviewResponseDTO.of(
            review,
            requestDTO.goodtags(),
            requestDTO.badtags(),
            reviewService.getTagRepository()
        );
        return ResponseEntity.ok(ResponseDTO.res(HttpStatus.CREATED, responseDTO));
    }
}
