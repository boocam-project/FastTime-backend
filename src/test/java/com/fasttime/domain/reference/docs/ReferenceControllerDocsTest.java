package com.fasttime.domain.reference.docs;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasttime.docs.RestDocsSupport;
import com.fasttime.domain.reference.controller.ReferenceController;
import com.fasttime.domain.reference.dto.request.ReferenceSearchRequestDto;
import com.fasttime.domain.reference.dto.response.ActivityPageResponseDto;
import com.fasttime.domain.reference.dto.response.CompetitionPageResponseDto;
import com.fasttime.domain.reference.dto.response.ReferenceResponseDto;
import com.fasttime.domain.reference.service.usecase.ReferenceServiceUseCase;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Pageable;
import org.springframework.restdocs.payload.JsonFieldType;

public class ReferenceControllerDocsTest extends RestDocsSupport {

    private final ReferenceServiceUseCase referenceServiceUseCase = mock(
        ReferenceServiceUseCase.class
    );

    @Override
    public ReferenceController initController() {
        return new ReferenceController(referenceServiceUseCase);
    }

    @DisplayName("대외활동 목록 조회 API 문서화")
    @Test
    void searchActivities() throws Exception {
        // given
        given(referenceServiceUseCase.searchActivities(
            any(ReferenceSearchRequestDto.class),
            any(Pageable.class)
        )).willReturn(ActivityPageResponseDto.builder()
            .totalPages(1)
            .isLastPage(true)
            .totalActivities(2)
            .activities(List.of(
                    ReferenceResponseDto.builder()
                        .id(1L)
                        .title("핀테크 IT 대외활동")
                        .organization("대외활동 협회")
                        .imageUrl("https://activities/1")
                        .build(),
                    ReferenceResponseDto.builder()
                        .id(2L)
                        .title("풀스택 IT 대외활동")
                        .organization("대외활동 협회")
                        .imageUrl("https://activities/2")
                        .build()
                )
            )
            .build()
        );

        // when, then
        mockMvc.perform(get("/api/v2/activities"))
            .andExpect(status().isOk())
            .andDo(document("activities-search",
                preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint()),
                queryParameters(
                    parameterWithName("keyword").description("대외활동 검색어").optional(),
                    parameterWithName("before").description("모집 전 대외활동 조회 여부").optional(),
                    parameterWithName("during").description("모집 중 대외활동 조회 여부").optional(),
                    parameterWithName("closed").description("모집 마감 대외활동 조회 여부").optional(),
                    parameterWithName("page").description("조회 페이지").optional(),
                    parameterWithName("pageSize").description("조회당 불러올 건 수").optional()),
                responseFields(
                    fieldWithPath("code").type(JsonFieldType.NUMBER).description("응답 상태코드"),
                    fieldWithPath("message").type(JsonFieldType.STRING).description("메시지"),
                    fieldWithPath("data").type(JsonFieldType.OBJECT).description("응답데이터"),
                    fieldWithPath("data.totalPages").type(JsonFieldType.NUMBER)
                        .description("총 페이지 수"),
                    fieldWithPath("data.isLastPage").type(JsonFieldType.BOOLEAN)
                        .description("마지막 페이지 여부"),
                    fieldWithPath("data.totalActivities").type(JsonFieldType.NUMBER)
                        .description("총 대외활동 수"),
                    fieldWithPath("data.activities").type(JsonFieldType.ARRAY)
                        .description("대외활동 배열"),
                    fieldWithPath("data.activities[].id").type(JsonFieldType.NUMBER)
                        .description("대외활동 식별자"),
                    fieldWithPath("data.activities[].title").type(JsonFieldType.STRING)
                        .description("대외활동 제목"),
                    fieldWithPath("data.activities[].organization").type(JsonFieldType.STRING)
                        .description("대외활동 주최"),
                    fieldWithPath("data.activities[].imageUrl").type(JsonFieldType.STRING)
                        .description("대외활동 이미지 URL"))));
    }

    @DisplayName("공모전 목록 조회 API 문서화")
    @Test
    void searchCompetitions() throws Exception {
        // given
        given(referenceServiceUseCase.searchCompetitions(
            any(ReferenceSearchRequestDto.class),
            any(Pageable.class)
        )).willReturn(CompetitionPageResponseDto.builder()
            .totalPages(1)
            .isLastPage(true)
            .totalCompetitions(2)
            .competitions(List.of(
                    ReferenceResponseDto.builder()
                        .id(1L)
                        .title("핀테크 IT 공모전")
                        .organization("공모전 협회")
                        .imageUrl("https://competitions/1")
                        .build(),
                    ReferenceResponseDto.builder()
                        .id(2L)
                        .title("풀스택 IT 공모전")
                        .organization("공모전 협회")
                        .imageUrl("https://competitions/2")
                        .build()
                )
            )
            .build()
        );

        // when, then
        mockMvc.perform(get("/api/v2/competitions"))
            .andExpect(status().isOk())
            .andDo(document("competitions-search",
                preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint()),
                queryParameters(
                    parameterWithName("keyword").description("공모전 검색어").optional(),
                    parameterWithName("before").description("모집 전 공모전 조회 여부").optional(),
                    parameterWithName("during").description("모집 중 공모전 조회 여부").optional(),
                    parameterWithName("closed").description("모집 마감 공모전 조회 여부").optional(),
                    parameterWithName("page").description("조회 페이지").optional(),
                    parameterWithName("pageSize").description("조회당 불러올 건 수").optional()),
                responseFields(
                    fieldWithPath("code").type(JsonFieldType.NUMBER).description("응답 상태코드"),
                    fieldWithPath("message").type(JsonFieldType.STRING).description("메시지"),
                    fieldWithPath("data").type(JsonFieldType.OBJECT).description("응답데이터"),
                    fieldWithPath("data.totalPages").type(JsonFieldType.NUMBER)
                        .description("총 페이지 수"),
                    fieldWithPath("data.isLastPage").type(JsonFieldType.BOOLEAN)
                        .description("마지막 페이지 여부"),
                    fieldWithPath("data.totalCompetitions").type(JsonFieldType.NUMBER)
                        .description("총 공모전 수"),
                    fieldWithPath("data.competitions").type(JsonFieldType.ARRAY)
                        .description("공모전 배열"),
                    fieldWithPath("data.competitions[].id").type(JsonFieldType.NUMBER)
                        .description("공모전 식별자"),
                    fieldWithPath("data.competitions[].title").type(JsonFieldType.STRING)
                        .description("공모전 제목"),
                    fieldWithPath("data.competitions[].organization").type(JsonFieldType.STRING)
                        .description("공모전 주최"),
                    fieldWithPath("data.competitions[].imageUrl").type(JsonFieldType.STRING)
                        .description("공모전 이미지 URL"))));
    }
}
