package com.fasttime.domain.review.unit.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.mockito.Mockito.*;

import com.fasttime.domain.member.exception.MemberNotFoundException;
import com.fasttime.domain.review.dto.request.ReviewRequestDTO;
import com.fasttime.domain.review.dto.response.BootcampReviewSummaryDTO;
import com.fasttime.domain.review.dto.response.ReviewResponseDTO;
import com.fasttime.domain.review.exception.ReviewAlreadyExistsException;
import com.fasttime.domain.review.exception.ReviewNotFoundException;
import com.fasttime.domain.review.exception.TagNotFoundException;
import com.fasttime.domain.review.exception.UnauthorizedAccessException;
import com.fasttime.util.ControllerUnitTestSupporter;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.mockito.stubbing.OngoingStubbing;
import org.springframework.http.MediaType;

class ReviewControllerTest extends ControllerUnitTestSupporter {

    @Nested
    @DisplayName("createReview()는")
    class Describe_createReview {

        @Test
        @DisplayName("유효한 리뷰 요청이 주어지면 리뷰를 생성한다.")
        void _willSuccess() throws Exception {
            // given
            ReviewRequestDTO requestDTO = new ReviewRequestDTO("테스트 리뷰 제목", Set.of(1L, 2L),
                Set.of(3L, 4L), 5, "테스트 리뷰 내용");
            ReviewResponseDTO responseDTO = new ReviewResponseDTO(1L, "패스트캠퍼스X야놀자 부트캠프",
                "테스트 리뷰 제목", Set.of("긍정 태그"), Set.of("부정 태그"), 5, "테스트 리뷰 내용");

            when(reviewService.createAndReturnReviewResponse(any(ReviewRequestDTO.class),
                anyLong())).thenReturn(responseDTO);

            // when, then
            mockMvc.perform(post("/api/v2/reviews")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.title").value(responseDTO.title()))
                .andExpect(jsonPath("$.data.content").value(responseDTO.content()));
        }

        @Test
        @DisplayName("권한이 없을 경우 실패한다.")
        void Unauthorized_willFail() throws Exception {
            // given
            OngoingStubbing<ReviewResponseDTO> requestDTO =
                when(reviewService.createAndReturnReviewResponse(any(ReviewRequestDTO.class),
                    anyLong()))
                    .thenThrow(new UnauthorizedAccessException());

            // when, then
            mockMvc.perform(post("/api/v2/reviews")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("존재하지 않는 사용자가 작성하면 실패한다.")
        void NotFound_willFail() throws Exception {
            // given
            OngoingStubbing<ReviewResponseDTO> requestDTO =
                when(reviewService.createAndReturnReviewResponse(any(ReviewRequestDTO.class),
                    anyLong()))
                    .thenThrow(new MemberNotFoundException());

            // when, then
            mockMvc.perform(post("/api/v2/reviews")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("중복 작성시 실패한다.")
        void ConflictBadRequest_willFail() throws Exception {
            // given
            ReviewRequestDTO requestDTO = new ReviewRequestDTO("테스트 리뷰 제목", Set.of(1L, 2L),
                Set.of(3L, 4L), 5, "테스트 리뷰 내용");
            when(
                reviewService.createAndReturnReviewResponse(any(ReviewRequestDTO.class), anyLong()))
                .thenThrow(new ReviewAlreadyExistsException());

            // when, then
            mockMvc.perform(post("/api/v2/reviews")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("존재하지 않는 태그를 사용할 경우 실패한다.")
        void TagBadRequest_willFail() throws Exception {
            // given
            ReviewRequestDTO requestDTO = new ReviewRequestDTO("테스트 리뷰 제목", Set.of(1L, 2L),
                Set.of(3L, 4L), 5, "테스트 리뷰 내용");
            when(
                reviewService.createAndReturnReviewResponse(any(ReviewRequestDTO.class), anyLong()))
                .thenThrow(new TagNotFoundException());

            // when, then
            mockMvc.perform(post("/api/v2/reviews")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("deleteReview()는")
    class Describe_deleteReview {

        @Test
        @DisplayName("성공한다.")
        void _willSuccess() throws Exception {
            // given
            Long reviewId = 1L;
            doNothing().when(reviewService).deleteReview(eq(reviewId), anyLong());

            // when, then
            mockMvc.perform(delete("/api/v2/reviews/{reviewId}", reviewId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("리뷰 요청이 완료되었습니다."));
        }

        @Test
        @DisplayName("존재하지 않는 리뷰를 삭제할 경우 실패한다.")
        void NotFound_willFail() throws Exception {
            // given
            Long reviewId = 1L;
            doThrow(new ReviewNotFoundException()).when(reviewService)
                .deleteReview(eq(reviewId), anyLong());

            // when, then
            mockMvc.perform(delete("/api/v2/reviews/{reviewId}", reviewId))
                .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("권한이 없는 사용자는 실패한다.")
        void Unauthorized_willFail() throws Exception {
            // given
            Long reviewId = 1L;
            doThrow(new UnauthorizedAccessException()).when(reviewService)
                .deleteReview(eq(reviewId), anyLong());

            // when, then
            mockMvc.perform(delete("/api/v2/reviews/{reviewId}", reviewId))
                .andExpect(status().isUnauthorized());
        }
    }

    @Nested
    @DisplayName("updateReview()는")
    class Describe_updateReview {

        @Test
        @DisplayName("성공한다.")
        void _willSuccess() throws Exception {
            // given
            Long reviewId = 1L;
            ReviewRequestDTO requestDTO = new ReviewRequestDTO("수정된 리뷰 제목", Set.of(1L), Set.of(2L),
                4, "수정된 리뷰 내용");
            ReviewResponseDTO responseDTO = new ReviewResponseDTO(reviewId, "패스트캠퍼스X야놀자 부트캠프",
                "수정된 리뷰 제목", Set.of("긍정 태그"), Set.of("부정 태그"), 4, "수정된 리뷰 내용");

            when(reviewService.updateAndReturnReviewResponse(eq(reviewId),
                any(ReviewRequestDTO.class), anyLong())).thenReturn(responseDTO);

            // when, then
            mockMvc.perform(put("/api/v2/reviews/{reviewId}", reviewId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.title").value(responseDTO.title()))
                .andExpect(jsonPath("$.data.content").value(responseDTO.content()));
        }

        @Test
        @DisplayName("존재하지 않는 리뷰를 수정할 경우 실패한다.")
        void NotFound_willFail() throws Exception {
            // given
            Long reviewId = 1L;
            ReviewRequestDTO requestDTO = new ReviewRequestDTO("수정된 리뷰 제목", Set.of(1L), Set.of(2L),
                4, "수정된 리뷰 내용");

            doThrow(new ReviewNotFoundException()).when(reviewService)
                .updateAndReturnReviewResponse(eq(reviewId), any(ReviewRequestDTO.class),
                    anyLong());

            // when, then
            mockMvc.perform(put("/api/v2/reviews/{reviewId}", reviewId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("권한이 없는 사용자는 실패한다.")
        void Unauthorized_willFail() throws Exception {
            // given
            Long reviewId = 1L;
            ReviewRequestDTO requestDTO = new ReviewRequestDTO("수정된 리뷰 제목", Set.of(1L), Set.of(2L),
                4, "수정된 리뷰 내용");

            doThrow(new UnauthorizedAccessException()).when(reviewService)
                .updateAndReturnReviewResponse(eq(reviewId), any(ReviewRequestDTO.class),
                    anyLong());

            // when, then
            mockMvc.perform(put("/api/v2/reviews/{reviewId}", reviewId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isUnauthorized());
        }
    }

    @Nested
    @DisplayName("리뷰 조회는")
    class Describe_get_all_Review {

        @Test
        @DisplayName("getReviews()를 성공한다.")
        void all_willSuccess() throws Exception {
            // given
            List<ReviewResponseDTO> reviews = Arrays.asList(
                new ReviewResponseDTO(1L, "부트캠프1", "리뷰1", Set.of("긍정 태그1"), Set.of("부정 태그1"), 5,
                    "리뷰 내용1"),
                new ReviewResponseDTO(2L, "부트캠프2", "리뷰2", Set.of("긍정 태그2"), Set.of("부정 태그2"), 4,
                    "리뷰 내용2")
            );

            when(reviewService.getSortedReviews(anyString())).thenReturn(reviews);

            // when, then
            mockMvc.perform(get("/api/v2/reviews/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].id").value(reviews.get(0).id()))
                .andExpect(jsonPath("$.data[0].bootcamp").value(reviews.get(0).bootcamp()))
                .andExpect(jsonPath("$.data[0].title").value(reviews.get(0).title()))
                .andExpect(jsonPath("$.data[0].rating").value(reviews.get(0).rating()))
                .andExpect(jsonPath("$.data[0].content").value(reviews.get(0).content()))
                .andExpect(jsonPath("$.data[1].id").value(reviews.get(1).id()))
                .andExpect(jsonPath("$.data[1].bootcamp").value(reviews.get(1).bootcamp()))
                .andExpect(jsonPath("$.data[1].title").value(reviews.get(1).title()))
                .andExpect(jsonPath("$.data[1].rating").value(reviews.get(1).rating()))
                .andExpect(jsonPath("$.data[1].content").value(reviews.get(1).content()));
        }

        @Test
        @DisplayName("getReviewsByBootcamp()를 성공한다.")
        void bootcamp_willSuccess() throws Exception {
            // given
            String bootcampName = "패스트캠퍼스X야놀자 부트캠프";
            List<ReviewResponseDTO> reviews = Arrays.asList(
                new ReviewResponseDTO(1L, bootcampName, "리뷰1", Set.of("긍정적 태그1"), Set.of("부정적 태그1"),
                    5, "리뷰 내용1"),
                new ReviewResponseDTO(2L, bootcampName, "리뷰2", Set.of("긍정적 태그2"), Set.of("부정적 태그2"),
                    4, "리뷰 내용2")
            );

            when(reviewService.getReviewsByBootcamp(eq(bootcampName), anyString())).thenReturn(
                reviews);

            // when, then
            mockMvc.perform(get("/api/v2/reviews/by-bootcamp")
                    .param("bootcamp", bootcampName))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].id").value(reviews.get(0).id()))
                .andExpect(jsonPath("$.data[0].bootcamp").value(reviews.get(0).bootcamp()))
                .andExpect(jsonPath("$.data[0].title").value(reviews.get(0).title()))
                .andExpect(jsonPath("$.data[0].rating").value(reviews.get(0).rating()))
                .andExpect(jsonPath("$.data[0].content").value(reviews.get(0).content()))
                .andExpect(jsonPath("$.data[1].id").value(reviews.get(1).id()))
                .andExpect(jsonPath("$.data[1].bootcamp").value(reviews.get(1).bootcamp()))
                .andExpect(jsonPath("$.data[1].title").value(reviews.get(1).title()))
                .andExpect(jsonPath("$.data[1].rating").value(reviews.get(1).rating()))
                .andExpect(jsonPath("$.data[1].content").value(reviews.get(1).content()));
        }

        @Test
        @DisplayName("getBootcampReviewSummaries()를 성공한다.")
        void bootcampSummary_willSuccess() throws Exception {
            // given
            List<BootcampReviewSummaryDTO> summaries = Arrays.asList(
                new BootcampReviewSummaryDTO("패스트캠퍼스X야놀자 부트캠프", 4.5, 10, 20,
                    Map.of(1L, 5L, 2L, 5L)),
                new BootcampReviewSummaryDTO("다른 부트캠프", 3.5, 8, 15, Map.of(3L, 4L, 4L, 4L))
            );

            when(reviewService.getBootcampReviewSummaries()).thenReturn(summaries);

            // when, then
            mockMvc.perform(get("/api/v2/reviews/by-bootcamp/summary"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].bootcamp").value(summaries.get(0).bootcamp()))
                .andExpect(
                    jsonPath("$.data[0].averageRating").value(summaries.get(0).averageRating()))
                .andExpect(
                    jsonPath("$.data[0].totalReviews").value(summaries.get(0).totalReviews()))
                .andExpect(jsonPath("$.data[0].totalTags").value(summaries.get(0).totalTags()))
                .andExpect(jsonPath("$.data[1].bootcamp").value(summaries.get(1).bootcamp()))
                .andExpect(
                    jsonPath("$.data[1].averageRating").value(summaries.get(1).averageRating()))
                .andExpect(
                    jsonPath("$.data[1].totalReviews").value(summaries.get(1).totalReviews()))
                .andExpect(jsonPath("$.data[1].totalTags").value(summaries.get(1).totalTags()));
        }
    }
}
