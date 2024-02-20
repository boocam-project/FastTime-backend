package com.fasttime.domain.review.docs;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import com.fasttime.docs.RestDocsSupport;
import com.fasttime.domain.review.controller.ReviewController;
import com.fasttime.domain.review.dto.request.ReviewRequestDTO;
import com.fasttime.domain.review.dto.response.ReviewResponseDTO;
import com.fasttime.domain.review.service.ReviewService;
import com.fasttime.global.util.SecurityUtil;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;

class ReviewControllerDocsTest extends RestDocsSupport {

    private final ReviewService reviewService = mock(ReviewService.class);
    private final SecurityUtil securityUtil = mock(SecurityUtil.class);

    @Override
    public Object initController() {
        return new ReviewController(reviewService, securityUtil);
    }

    @DisplayName("리뷰 작성 API 문서화")
    @Test
    void createReview() throws Exception {

        ReviewRequestDTO requestDto = new ReviewRequestDTO("패스트캠퍼스X야놀자 솔직후기", Set.of(1L, 2L),
            Set.of(18L, 19L), 3, "전체적으로 아쉬웠습니다.");

        when(reviewService.createAndReturnReviewResponse(any(ReviewRequestDTO.class), anyLong()))
            .thenReturn(new ReviewResponseDTO(1L, "패스트캠퍼스X야놀자 부트캠프", "패스트캠퍼스X야놀자 솔직후기",
                Set.of("체계적인 커리큘럼", "퀄리티 있는 강의"), Set.of("부족한 혜택", "오프라인"), 3, "전체적으로 아쉬웠습니다."));

        mockMvc.perform(post("/api/v2/reviews")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto)))
            .andExpect(status().isCreated())
            .andDo(document("review-create",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                requestFields(
                    fieldWithPath("title").type(JsonFieldType.STRING).description("리뷰 제목"),
                    fieldWithPath("goodtags").type(JsonFieldType.ARRAY).description("좋은 태그 ID 목록"),
                    fieldWithPath("badtags").type(JsonFieldType.ARRAY).description("나쁜 태그 ID 목록"),
                    fieldWithPath("rating").type(JsonFieldType.NUMBER).description("리뷰 평점"),
                    fieldWithPath("content").type(JsonFieldType.STRING).description("리뷰 내용")
                ),
                responseFields(
                    fieldWithPath("code").type(JsonFieldType.NUMBER).description("응답 코드"),
                    fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                    fieldWithPath("data.id").type(JsonFieldType.NUMBER).description("리뷰 ID"),
                    fieldWithPath("data.bootcamp").type(JsonFieldType.STRING)
                        .description("부트캠프 이름"),
                    fieldWithPath("data.title").type(JsonFieldType.STRING).description("리뷰 제목"),
                    fieldWithPath("data.goodtags").type(JsonFieldType.ARRAY)
                        .description("좋아요 태그 목록"),
                    fieldWithPath("data.badtags").type(JsonFieldType.ARRAY)
                        .description("나빠요 태그 목록"),
                    fieldWithPath("data.rating").type(JsonFieldType.NUMBER).description("리뷰 평점"),
                    fieldWithPath("data.content").type(JsonFieldType.STRING).description("리뷰 내용")
                )
            ));
    }

    @DisplayName("리뷰 삭제 API 문서화")
    @Test
    void deleteReview() throws Exception {

        doNothing().when(reviewService).deleteReview(anyLong(), anyLong());

        this.mockMvc.perform(delete("/api/v2/reviews/{reviewId}", 1L)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andDo(document("review-delete",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                responseFields(
                    fieldWithPath("code").type(JsonFieldType.NUMBER).description("응답 코드"),
                    fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                    fieldWithPath("data").type(JsonFieldType.NULL).description("데이터 (null)")
                )
            ));
    }

    @DisplayName("리뷰 수정 API 문서화")
    @Test
    void updateReview() throws Exception {

        ReviewRequestDTO requestDto = new ReviewRequestDTO("수정된 리뷰 제목", Set.of(2L),
            Set.of(18L, 19L), 5, "수정된 리뷰 내용");

        ReviewResponseDTO responseDTO = new ReviewResponseDTO(1L, "패스트캠퍼스X야놀자 부트캠프", "수정된 리뷰 제목",
            Set.of("강의가 좋아요"), Set.of("부족한 혜택", "오프라인"), 5, "수정된 리뷰 내용");

        when(reviewService.updateAndReturnReviewResponse(anyLong(), any(ReviewRequestDTO.class),
            anyLong())).thenReturn(responseDTO);

        this.mockMvc.perform(put("/api/v2/reviews/{reviewId}", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto)))
            .andExpect(status().isOk())
            .andDo(document("review-update",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                requestFields(
                    fieldWithPath("title").type(JsonFieldType.STRING).description("리뷰 제목"),
                    fieldWithPath("goodtags").type(JsonFieldType.ARRAY).description("좋은 태그 ID 목록"),
                    fieldWithPath("badtags").type(JsonFieldType.ARRAY).description("나쁜 태그 ID 목록"),
                    fieldWithPath("rating").type(JsonFieldType.NUMBER).description("리뷰 평점"),
                    fieldWithPath("content").type(JsonFieldType.STRING).description("리뷰 내용")
                ),
                responseFields(
                    fieldWithPath("code").type(JsonFieldType.NUMBER).description("응답 코드"),
                    fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                    fieldWithPath("data.id").type(JsonFieldType.NUMBER).description("리뷰 ID"),
                    fieldWithPath("data.bootcamp").type(JsonFieldType.STRING)
                        .description("부트캠프 이름"),
                    fieldWithPath("data.title").type(JsonFieldType.STRING).description("리뷰 제목"),
                    fieldWithPath("data.goodtags").type(JsonFieldType.ARRAY)
                        .description("좋아요 태그 목록"),
                    fieldWithPath("data.badtags").type(JsonFieldType.ARRAY)
                        .description("나빠요 태그 목록"),
                    fieldWithPath("data.rating").type(JsonFieldType.NUMBER).description("리뷰 평점"),
                    fieldWithPath("data.content").type(JsonFieldType.STRING).description("리뷰 내용")
                )
            ));
    }

    @DisplayName("전체 리뷰 조회 API 문서화")
    @Test
    void getReviews() throws Exception {
        List<ReviewResponseDTO> reviews = List.of(
            new ReviewResponseDTO(1L, "패스트캠퍼스X야놀자 부트캠프", "리뷰 제목 1", Set.of("친절해요"), Set.of("불친절해요"),
                5, "리뷰 내용 1"),
            new ReviewResponseDTO(2L, "다른 부트캠프", "리뷰 제목 2", Set.of("강의가 좋아요"), Set.of("피드백이 느려요"),
                4, "리뷰 내용 2")
        );

        when(reviewService.getSortedReviews(anyString())).thenReturn(reviews);

        mockMvc.perform(get("/api/v2/reviews/all"))
            .andExpect(status().isOk())
            .andDo(document("reviews-get-all",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                responseFields(
                    fieldWithPath("code").type(JsonFieldType.NUMBER).description("응답 코드"),
                    fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                    fieldWithPath("data[].id").type(JsonFieldType.NUMBER).description("리뷰 ID"),
                    fieldWithPath("data[].bootcamp").type(JsonFieldType.STRING)
                        .description("부트캠프 이름"),
                    fieldWithPath("data[].title").type(JsonFieldType.STRING).description("리뷰 제목"),
                    fieldWithPath("data[].goodtags").type(JsonFieldType.ARRAY)
                        .description("좋아요 태그 목록"),
                    fieldWithPath("data[].badtags").type(JsonFieldType.ARRAY)
                        .description("나빠요 태그 목록"),
                    fieldWithPath("data[].rating").type(JsonFieldType.NUMBER).description("리뷰 평점"),
                    fieldWithPath("data[].content").type(JsonFieldType.STRING).description("리뷰 내용")
                )
            ));
    }

    @DisplayName("부트캠프별 리뷰 조회 API 문서화")
    @Test
    void getReviewsByBootcamp() throws Exception {
        List<ReviewResponseDTO> reviews = List.of(
            new ReviewResponseDTO(1L, "패스트캠퍼스X야놀자 부트캠프", "리뷰 제목 1", Set.of("친절해요"), Set.of("불친절해요"),
                5, "리뷰 내용 1"),
            new ReviewResponseDTO(2L, "패스트캠퍼스X야놀자 부트캠프", "리뷰 제목 2", Set.of("강의가 좋아요"),
                Set.of("피드백이 느려요"), 4, "리뷰 내용 2")
        );

        when(reviewService.getReviewsByBootcamp(anyString(), anyString())).thenReturn(reviews);

        mockMvc.perform(get("/api/v2/reviews/by-bootcamp")
                .param("bootcamp", "패스트캠퍼스X야놀자 부트캠프")
                .param("sortBy", "rating"))
            .andExpect(status().isOk())
            .andDo(document("reviews-get-bootcamp",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                responseFields(
                    fieldWithPath("code").type(JsonFieldType.NUMBER).description("응답 코드"),
                    fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                    fieldWithPath("data[].id").type(JsonFieldType.NUMBER).description("리뷰 ID"),
                    fieldWithPath("data[].bootcamp").type(JsonFieldType.STRING)
                        .description("부트캠프 이름"),
                    fieldWithPath("data[].title").type(JsonFieldType.STRING).description("리뷰 제목"),
                    fieldWithPath("data[].goodtags").type(JsonFieldType.ARRAY)
                        .description("좋아요 태그 목록"),
                    fieldWithPath("data[].badtags").type(JsonFieldType.ARRAY)
                        .description("나빠요 태그 목록"),
                    fieldWithPath("data[].rating").type(JsonFieldType.NUMBER).description("리뷰 평점"),
                    fieldWithPath("data[].content").type(JsonFieldType.STRING).description("리뷰 내용")
                )
            ));
    }
}
