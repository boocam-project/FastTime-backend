package com.fasttime.domain.review.controller;

import com.fasttime.domain.review.dto.request.ReviewRequestDTO;
import com.fasttime.domain.review.dto.response.BootcampReviewSummaryDTO;
import com.fasttime.domain.review.dto.response.ReviewResponseDTO;
import com.fasttime.domain.review.dto.response.TagSummaryDTO;
import com.fasttime.domain.review.service.ReviewService;
import com.fasttime.global.util.ResponseDTO;
import com.fasttime.global.util.SecurityUtil;
import jakarta.validation.Valid;
import java.util.LinkedHashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
        @Valid @RequestBody ReviewRequestDTO requestDTO) {
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

    @GetMapping
    public ResponseEntity<ResponseDTO<Map<String, Object>>> getReviews(
        @RequestParam(required = false) String bootcamp,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(required = false, defaultValue = "createdAt") String sortBy) {

        Pageable pageable = PageRequest.of(page, 6, Sort.by(sortBy).descending());
        Page<ReviewResponseDTO> reviews = reviewService.getSortedReviews(bootcamp, pageable);
        Map<String, Object> responseMap = createPaginationResponse(reviews);
        return ResponseEntity.ok(
            ResponseDTO.res(HttpStatus.OK, REVIEW_SUCCESS_MESSAGE, responseMap));

    }

    @GetMapping("/summary")
    public ResponseEntity<ResponseDTO<Map<String, Object>>> getBootcampReviewSummaries(
        @RequestParam(defaultValue = "0") int page) {

        Pageable pageable = PageRequest.of(page, 10);
        Page<BootcampReviewSummaryDTO> summaries = reviewService.getBootcampReviewSummaries(
            pageable);
        Map<String, Object> responseMap = createPaginationResponse(summaries);
        return ResponseEntity.ok(
            ResponseDTO.res(HttpStatus.OK, REVIEW_SUCCESS_MESSAGE, responseMap));

    }

    @GetMapping("/tag-graph")
    public ResponseEntity<ResponseDTO<TagSummaryDTO>> getTagCountsByBootcamp(
        @RequestParam String bootcamp) {
        TagSummaryDTO tagData = reviewService.getBootcampTagData(bootcamp);
        return ResponseEntity.ok(ResponseDTO.res(HttpStatus.OK, REVIEW_SUCCESS_MESSAGE, tagData));
    }

    private Map<String, Object> createPaginationResponse(Page<?> page) {
        Map<String, Object> responseMap = new LinkedHashMap<>();
        responseMap.put("currentPage", page.getNumber() + 1);
        responseMap.put("totalPages", page.getTotalPages());
        responseMap.put("currentElements", page.getNumberOfElements());
        responseMap.put("totalElements", page.getTotalElements());
        responseMap.put("reviews", page.getContent());
        return responseMap;
    }
}
