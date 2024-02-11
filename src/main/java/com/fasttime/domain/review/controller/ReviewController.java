package com.fasttime.domain.review.controller;

import com.fasttime.domain.review.dto.request.ReviewRequestDTO;
import com.fasttime.domain.review.dto.response.ReviewResponseDTO;
import com.fasttime.domain.review.service.ReviewService;
import com.fasttime.global.util.ResponseDTO;
import com.fasttime.global.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v2/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;
    private final SecurityUtil securityUtil;

    private static final String REVIEW_SUCCESS_MESSAGE = "리뷰 요청이 완료되었습니다.";

    @PostMapping
    public ResponseEntity<ResponseDTO<ReviewResponseDTO>> createReview(
        @RequestBody ReviewRequestDTO requestDTO) {
        Long memberId = securityUtil.getCurrentMemberId();
        ReviewResponseDTO responseDTO = reviewService.createAndReturnReviewResponse(requestDTO,
            memberId);
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(ResponseDTO.res(HttpStatus.CREATED, REVIEW_SUCCESS_MESSAGE, responseDTO));
    }

    @DeleteMapping("/{reviewId}")
    public ResponseEntity<ResponseDTO<?>> deleteReview(@PathVariable Long reviewId) {
        Long memberId = securityUtil.getCurrentMemberId();
        reviewService.deleteReview(reviewId, memberId);
        return ResponseEntity.ok(ResponseDTO.res(HttpStatus.OK, REVIEW_SUCCESS_MESSAGE));
    }

    @PutMapping("/{reviewId}")
    public ResponseEntity<ResponseDTO<ReviewResponseDTO>> updateReview(
        @PathVariable Long reviewId,
        @RequestBody ReviewRequestDTO requestDTO) {
        Long memberId = securityUtil.getCurrentMemberId();
        ReviewResponseDTO responseDTO = reviewService.updateAndReturnReviewResponse(reviewId,
            requestDTO, memberId);
        return ResponseEntity.ok(
            ResponseDTO.res(HttpStatus.OK, REVIEW_SUCCESS_MESSAGE, responseDTO));
    }
}
