package com.fasttime.domain.review.docs;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import com.fasttime.docs.RestDocsSupport;
import com.fasttime.domain.review.controller.ReviewController;
import com.fasttime.domain.review.dto.request.ReviewRequestDTO;
import com.fasttime.domain.review.dto.response.ReviewResponseDTO;
import com.fasttime.domain.review.service.ReviewService;
import com.fasttime.global.util.SecurityUtil;
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

        ReviewRequestDTO requestDto = new ReviewRequestDTO("test 부트캠프 리뷰", Set.of(1L, 2L), Set.of(3L, 4L), 5, "뭐야");


        when(reviewService.createAndReturnReviewResponse(any(ReviewRequestDTO.class), anyLong()))
            .thenReturn(new ReviewResponseDTO(7L, "다른 부트캠프", "test 부트캠프 리뷰", Set.of("친절해요", "강의가 좋아요"), Set.of("불친절해요", "피드백이 느려요"), 5, "뭐야"));

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
                    fieldWithPath("data.bootcamp").type(JsonFieldType.STRING).description("부트캠프 이름"),
                    fieldWithPath("data.title").type(JsonFieldType.STRING).description("리뷰 제목"),
                    fieldWithPath("data.goodtags").type(JsonFieldType.ARRAY).description("좋아요 태그 목록"),
                    fieldWithPath("data.badtags").type(JsonFieldType.ARRAY).description("나빠요 태그 목록"),
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
            .andDo(document("reviews-delete",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                responseFields(
                    fieldWithPath("code").type(JsonFieldType.NUMBER).description("응답 코드"),
                    fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                    fieldWithPath("data").type(JsonFieldType.NULL).description("데이터 (null)")
                )
            ));
    }
}
