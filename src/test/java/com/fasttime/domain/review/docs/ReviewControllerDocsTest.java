package com.fasttime.domain.review.docs;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.put;
import static org.springframework.restdocs.request.RequestDocumentation.*;

import com.fasttime.docs.RestDocsSupport;
import com.fasttime.domain.review.controller.ReviewController;
import com.fasttime.domain.review.dto.request.ReviewRequestDTO;
import com.fasttime.domain.review.dto.response.BootcampReviewSummaryDTO;
import com.fasttime.domain.review.dto.response.ReviewResponseDTO;
import com.fasttime.domain.review.dto.response.TagSummaryDTO;
import com.fasttime.domain.review.service.ReviewService;
import com.fasttime.global.util.SecurityUtil;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
            Set.of(8L, 9L), 3, "전체적으로 아쉬웠습니다.");

        when(reviewService.createAndReturnReviewResponse(any(ReviewRequestDTO.class), anyLong()))
            .thenReturn(new ReviewResponseDTO(1L, "말하는 감자", "패스트캠퍼스X야놀자 부트캠프", "패스트캠퍼스X야놀자 솔직후기",
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
                    fieldWithPath("data.authorNickname").type(JsonFieldType.STRING)
                        .description("작성자 닉네임"),
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
                pathParameters(
                    parameterWithName("reviewId").description("리뷰 id")
                ),
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
            Set.of(8L, 9L), 5, "수정된 리뷰 내용");

        ReviewResponseDTO responseDTO = new ReviewResponseDTO(1L, "말하는 감자", "패스트캠퍼스X야놀자 부트캠프",
            "수정된 리뷰 제목", Set.of("강의가 좋아요"), Set.of("부족한 혜택", "오프라인"), 5, "수정된 리뷰 내용");

        when(reviewService.updateAndReturnReviewResponse(eq(1L), any(ReviewRequestDTO.class),
            anyLong()))
            .thenReturn(responseDTO);

        mockMvc.perform(put("/api/v2/reviews/{reviewId}", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto)))
            .andExpect(status().isOk())
            .andDo(document("review-update",

                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                pathParameters(
                    parameterWithName("reviewId").description("리뷰 id")
                ),
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
                    fieldWithPath("data.authorNickname").type(JsonFieldType.STRING)
                        .description("작성자 닉네임"),
                    fieldWithPath("data.authorNickname").type(JsonFieldType.STRING)
                        .description("작성자 닉네임"),
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
            new ReviewResponseDTO(1L, "말하는 감자", "부트캠프1", "리뷰 제목 1", Set.of("친절해요"),
                Set.of("불친절해요"), 5, "리뷰 내용 1"),
            new ReviewResponseDTO(2L, "말하는 고구마", "부트캠프2", "리뷰 제목 2", Set.of("강의가 좋아요"),
                Set.of("피드백이 느려요"), 4, "리뷰 내용 2")
        );
        Page<ReviewResponseDTO> reviewsPage = new PageImpl<>(reviews);

        when(reviewService.getSortedReviews(anyString(),  any(Pageable.class))).thenReturn(reviewsPage)
            .thenReturn(reviewsPage);

        mockMvc.perform(get("/api/v2/reviews")
                .queryParam("bootcamp", "")
                .queryParam("page", "0")
                .queryParam("size", "6")
                .queryParam("sortBy", "rating"))
            .andExpect(status().isOk())
            .andDo(document("reviews-get-all",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                queryParameters(
                    parameterWithName("bootcamp").description("부트캠프 이름"),
                    parameterWithName("page").description("페이지 번호"),
                    parameterWithName("size").description("페이지당 항목 수"),
                    parameterWithName("sortBy").description("정렬 기준").optional()
                ),
                responseFields(
                    fieldWithPath("code").type(JsonFieldType.NUMBER).description("응답 코드"),
                    fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                    subsectionWithPath("data.reviews[]").type(JsonFieldType.ARRAY).description("페이지네이션된 리뷰 목록"),
                    fieldWithPath("data.reviews[].id").type(JsonFieldType.NUMBER).description("리뷰 ID"),
                    fieldWithPath("data.reviews[].authorNickname").type(JsonFieldType.STRING).description("작성자 닉네임"),
                    fieldWithPath("data.reviews[].bootcamp").type(JsonFieldType.STRING).description("부트캠프 이름"),
                    fieldWithPath("data.reviews[].title").type(JsonFieldType.STRING).description("리뷰 제목"),
                    fieldWithPath("data.reviews[].goodtags").type(JsonFieldType.ARRAY).description("좋은 태그 목록"),
                    fieldWithPath("data.reviews[].badtags").type(JsonFieldType.ARRAY).description("나쁜 태그 목록"),
                    fieldWithPath("data.reviews[].rating").type(JsonFieldType.NUMBER).description("리뷰 평점"),
                    fieldWithPath("data.reviews[].content").type(JsonFieldType.STRING).description("리뷰 내용"),
                    fieldWithPath("data.currentPage").type(JsonFieldType.NUMBER).description("현재 페이지"),
                    fieldWithPath("data.totalPages").type(JsonFieldType.NUMBER).description("총 페이지 수"),
                    fieldWithPath("data.currentElements").type(JsonFieldType.NUMBER).description("현재 페이지의 리뷰 수"),
                    fieldWithPath("data.totalElements").type(JsonFieldType.NUMBER).description("전체 리뷰 수")
                )
            ));
    }

    @DisplayName("부트캠프별 리뷰 조회 API 문서화")
    @Test
    void getReviewsByBootcamp() throws Exception {
        List<ReviewResponseDTO> reviews = List.of(
            new ReviewResponseDTO(1L, "말하는 감자", "패스트캠퍼스X야놀자 부트캠프", "리뷰 제목 1", Set.of("친절해요"),
                Set.of("불친절해요"), 5, "리뷰 내용 1"),
            new ReviewResponseDTO(2L, "말하는 고구마", "패스트캠퍼스X야놀자 부트캠프", "리뷰 제목 2", Set.of("강의가 좋아요"),
                Set.of("피드백이 느려요"), 4, "리뷰 내용 2")
        );
        Page<ReviewResponseDTO> reviewsPage = new PageImpl<>(reviews);

        when(reviewService.getSortedReviews(eq("패스트캠퍼스X야놀자 부트캠프"), any(Pageable.class)))
            .thenReturn(reviewsPage);

        mockMvc.perform(get("/api/v2/reviews")
                .queryParam("bootcamp", "패스트캠퍼스X야놀자 부트캠프")
                .queryParam("page", "0")
                .queryParam("size", "6")
                .queryParam("sortBy", "rating"))
            .andExpect(status().isOk())
            .andDo(document("reviews-get-bootcamp",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                queryParameters(
                    parameterWithName("bootcamp").description("부트캠프 이름"),
                    parameterWithName("page").description("페이지 번호"),
                    parameterWithName("size").description("페이지당 항목 수"),
                    parameterWithName("sortBy").description("정렬 기준").optional()
                ),
                responseFields(
                    fieldWithPath("code").type(JsonFieldType.NUMBER).description("응답 코드"),
                    fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                    subsectionWithPath("data.reviews[]").type(JsonFieldType.ARRAY).description("페이지네이션된 리뷰 목록"),
                    fieldWithPath("data.reviews[].id").type(JsonFieldType.NUMBER).description("리뷰 ID"),
                    fieldWithPath("data.reviews[].authorNickname").type(JsonFieldType.STRING).description("작성자 닉네임"),
                    fieldWithPath("data.reviews[].bootcamp").type(JsonFieldType.STRING).description("부트캠프 이름"),
                    fieldWithPath("data.reviews[].title").type(JsonFieldType.STRING).description("리뷰 제목"),
                    fieldWithPath("data.reviews[].goodtags").type(JsonFieldType.ARRAY).description("좋은 태그 목록"),
                    fieldWithPath("data.reviews[].badtags").type(JsonFieldType.ARRAY).description("나쁜 태그 목록"),
                    fieldWithPath("data.reviews[].rating").type(JsonFieldType.NUMBER).description("리뷰 평점"),
                    fieldWithPath("data.reviews[].content").type(JsonFieldType.STRING).description("리뷰 내용"),
                    fieldWithPath("data.currentPage").type(JsonFieldType.NUMBER).description("현재 페이지"),
                    fieldWithPath("data.totalPages").type(JsonFieldType.NUMBER).description("총 페이지 수"),
                    fieldWithPath("data.currentElements").type(JsonFieldType.NUMBER).description("현재 페이지의 리뷰 수"),
                    fieldWithPath("data.totalElements").type(JsonFieldType.NUMBER).description("전체 리뷰 수")
                )
            ));
    }

    @DisplayName("부트캠프별 리뷰 요약 조회 API 문서화")
    @Test
    void getBootcampReviewSummaries() throws Exception {
        List<BootcampReviewSummaryDTO> summaries = List.of(
            new BootcampReviewSummaryDTO("야놀자x패스트캠퍼스 부트캠프", 3.0, 7l),
            new BootcampReviewSummaryDTO("다른 부트캠프", 4.5, 2l)
        );

        int page = 0;
        int size = 10;
        Pageable pageable = PageRequest.of(page, size);

        Page<BootcampReviewSummaryDTO> pagedSummaries = new PageImpl<>(summaries, pageable, summaries.size());

        when(reviewService.getBootcampReviewSummaries(pageable)).thenReturn(pagedSummaries);

        mockMvc.perform(get("/api/v2/reviews/summary")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andDo(document("reviews-get-summary",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                responseFields(
                    fieldWithPath("code").type(JsonFieldType.NUMBER).description("응답 코드"),
                    fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                    subsectionWithPath("data").type(JsonFieldType.OBJECT)
                        .description("응답 데이터"),
                    subsectionWithPath("data.reviews").type(JsonFieldType.ARRAY)
                        .description("부트캠프별 리뷰 요약 목록"),
                    fieldWithPath("data.reviews[].bootcamp").type(JsonFieldType.STRING)
                        .description("부트캠프 이름"),
                    fieldWithPath("data.reviews[].averageRating").type(JsonFieldType.NUMBER)
                        .description("평균 평점"),
                    fieldWithPath("data.reviews[].totalReviews").type(JsonFieldType.NUMBER)
                        .description("총 리뷰 수"),
                    fieldWithPath("data.currentPage").type(JsonFieldType.NUMBER)
                        .description("현재 페이지"),
                    fieldWithPath("data.totalPages").type(JsonFieldType.NUMBER)
                        .description("총 페이지 수"),
                    fieldWithPath("data.currentElements").type(JsonFieldType.NUMBER)
                        .description("현재 페이지의 리뷰 요약 수"),
                    fieldWithPath("data.totalElements").type(JsonFieldType.NUMBER)
                        .description("전체 리뷰 요약 수")
                )
            ));
    }

    @DisplayName("부트캠프별 태그 통계 조회 API 문서화")
    @Test
    void getTagCountsByBootcamp() throws Exception {
        TagSummaryDTO tagSummary = new TagSummaryDTO(10, Map.of(1L, 5L, 2L, 5L));

        when(reviewService.getBootcampTagData(eq("패스트캠퍼스X야놀자 부트캠프"))).thenReturn(tagSummary);

        mockMvc.perform(get("/api/v2/reviews/tag-graph")
                .queryParam("bootcamp", "패스트캠퍼스X야놀자 부트캠프"))
            .andExpect(status().isOk())
            .andDo(document("reviews-get-tag-graph",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                queryParameters(
                    parameterWithName("bootcamp").description("부트캠프 이름")
                ),
                responseFields(
                    fieldWithPath("code").type(JsonFieldType.NUMBER).description("응답 코드"),
                    fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                    fieldWithPath("data.totalTags").type(JsonFieldType.NUMBER)
                        .description("총 태그 수"),
                    fieldWithPath("data.tagCounts").type(JsonFieldType.OBJECT)
                        .description("태그별 사용 횟수"),
                    fieldWithPath("data.tagCounts.*").type(JsonFieldType.NUMBER)
                        .description("개별 태그별 사용 횟수")
                )
            ));
    }
}
