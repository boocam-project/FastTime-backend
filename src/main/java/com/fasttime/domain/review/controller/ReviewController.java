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

    @PostMapping
    public ResponseEntity<ResponseDTO<ReviewResponseDTO>> createReview(
        @RequestBody ReviewRequestDTO requestDTO) {
        Long memberId = securityUtil.getCurrentMemberId();
        Review review = reviewService.createReview(requestDTO, memberId);

        ReviewResponseDTO responseDTO = ReviewResponseDTO.of(
            review,
            requestDTO.goodtags(),
            requestDTO.badtags(),
            reviewService.getTagRepository()
        );
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(ResponseDTO.res(HttpStatus.CREATED, "리뷰작성이 완료되었습니다.", responseDTO));
    }

    @DeleteMapping("/{reviewId}")
    public ResponseEntity<ResponseDTO<?>> deleteReview(@PathVariable Long reviewId) {
        Long memberId = securityUtil.getCurrentMemberId();
        reviewService.deleteReview(reviewId, memberId);
        return ResponseEntity.ok(ResponseDTO.res(HttpStatus.OK, "리뷰삭제가 완료되었습니다."));
    }

    @PutMapping("/{reviewId}")
    public ResponseEntity<ResponseDTO<ReviewResponseDTO>> updateReview(
        @PathVariable Long reviewId,
        @RequestBody ReviewRequestDTO requestDTO) {

        Review updatedReview = reviewService.updateReview(reviewId, requestDTO);
        ReviewResponseDTO responseDTO = ReviewResponseDTO.of(
            updatedReview,
            requestDTO.goodtags(),
            requestDTO.badtags(),
            reviewService.getTagRepository()
        );
        return ResponseEntity.ok(ResponseDTO.res(HttpStatus.OK, "리뷰수정이 완료되었습니다.", responseDTO));
    }
}
