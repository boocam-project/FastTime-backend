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
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasttime.docs.RestDocsSupport;
import com.fasttime.domain.reference.controller.ReferenceController;
import com.fasttime.domain.reference.dto.request.ReferenceSearchRequestDto;
import com.fasttime.domain.reference.dto.response.ActivityPageResponseDto;
import com.fasttime.domain.reference.dto.response.ActivityResponseDto;
import com.fasttime.domain.reference.dto.response.CompetitionPageResponseDto;
import com.fasttime.domain.reference.dto.response.CompetitionResponseDto;
import com.fasttime.domain.reference.dto.response.ReferenceResponseDto;
import com.fasttime.domain.reference.service.usecase.ReferenceServiceUseCase;
import java.time.LocalDate;
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
                        .dDay(1)
                        .build(),
                    ReferenceResponseDto.builder()
                        .id(2L)
                        .title("풀스택 IT 대외활동")
                        .organization("대외활동 협회")
                        .imageUrl("https://activities/2")
                        .dDay(2)
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
                        .description("대외활동 이미지 URL"),
                    fieldWithPath("data.activities[].dDay").type(JsonFieldType.NUMBER)
                        .description("접수 마감까지 남은 일수 (접수 마감 이후라면 지난 일수를 음수로 나타냄)")
                )
            ));
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
                        .dDay(1)
                        .build(),
                    ReferenceResponseDto.builder()
                        .id(2L)
                        .title("풀스택 IT 공모전")
                        .organization("공모전 협회")
                        .imageUrl("https://competitions/2")
                        .dDay(2)
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
                        .description("공모전 이미지 URL"),
                    fieldWithPath("data.competitions[].dDay").type(JsonFieldType.NUMBER)
                        .description("접수 마감까지 남은 일수 (접수 마감 이후라면 지난 일수를 음수로 나타냄)")
                )
            ));
    }

    @DisplayName("대외활동 상세 조회 API 문서화")
    @Test
    void getActivity() throws Exception {
        // given
        given(referenceServiceUseCase.getActivity(any(Long.TYPE))).willReturn(
            ActivityResponseDto.builder()
                .title("한국 전력 공사 IT 대외활동")
                .organization("한국전력공사")
                .corporateType("중소기업")
                .participate("대학생")
                .startDate(LocalDate.of(2023, 1, 2))
                .endDate(LocalDate.of(2023, 2, 3))
                .period("2023-01-02 ~ 2023-02-31")
                .recruitment(30)
                .area("부산")
                .preferredSkill("컴퓨터활용자격증보유")
                .homepageUrl("https://blog.naver.com/gyeryongcity1/222972814128")
                .field("멘토링")
                .activityBenefit("실무 교육, 수료증 및 인증서")
                .bonusBenefit("훈련장려금 지급")
                .description("""
                    🔥전액 무료🔥 딱 한 달만에
                    최신 '반도체 실무 스킬' 쌓는 법!
                    <반도체 실무 온라인 실습과정 : 소자+핵심공정> 강의
                    """)
                .imageUrl("“https://images/urls/1”")
                .build()
        );

        // when, then
        mockMvc.perform(get("/api/v2/activities/{activityId}", 1L))
            .andExpect(status().isOk())
            .andDo(document("activity-get",
                preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint()),
                pathParameters(
                    parameterWithName("activityId").description("대외활동 식별자").optional()),
                responseFields(
                    fieldWithPath("code").type(JsonFieldType.NUMBER).description("응답 상태코드"),
                    fieldWithPath("message").type(JsonFieldType.STRING).description("메시지"),
                    fieldWithPath("data").type(JsonFieldType.OBJECT).description("응답데이터"),
                    fieldWithPath("data.title").type(JsonFieldType.STRING)
                        .description("대외활동 제목"),
                    fieldWithPath("data.organization").type(JsonFieldType.STRING)
                        .description("대외활동 주최"),
                    fieldWithPath("data.corporateType").type(JsonFieldType.STRING)
                        .description("대외활동 주최 기업 형태"),
                    fieldWithPath("data.participate").type(JsonFieldType.STRING)
                        .description("대외활동 참여 대상"),
                    fieldWithPath("data.startDate").type(JsonFieldType.STRING)
                        .description("대외활동 접수 시작일"),
                    fieldWithPath("data.endDate").type(JsonFieldType.STRING)
                        .description("대외활동 접수 마감일"),
                    fieldWithPath("data.period").type(JsonFieldType.STRING)
                        .description("대외활동 활동 기간"),
                    fieldWithPath("data.recruitment").type(JsonFieldType.NUMBER)
                        .description("대외활동 모집 인원"),
                    fieldWithPath("data.area").type(JsonFieldType.STRING)
                        .description("대외활동 활동 지역"),
                    fieldWithPath("data.preferredSkill").type(JsonFieldType.STRING)
                        .description("대외활동 우대 역량"),
                    fieldWithPath("data.homepageUrl").type(JsonFieldType.STRING)
                        .description("대외활동 홈페이지 URL"),
                    fieldWithPath("data.field").type(JsonFieldType.STRING)
                        .description("대외활동 활동 분야"),
                    fieldWithPath("data.activityBenefit").type(JsonFieldType.STRING)
                        .description("대외활동 활동 혜택"),
                    fieldWithPath("data.bonusBenefit").type(JsonFieldType.STRING)
                        .description("대외활동 추가 혜택"),
                    fieldWithPath("data.description").type(JsonFieldType.STRING)
                        .description("대외활동 상세 내용"),
                    fieldWithPath("data.imageUrl").type(JsonFieldType.STRING)
                        .description("대외활동 이미지 URL"))));
    }

    @DisplayName("공모전 상세 조회 API 문서화")
    @Test
    void getCompetition() throws Exception {
        // given
        given(referenceServiceUseCase.getCompetition(any(Long.TYPE))).willReturn(
            CompetitionResponseDto.builder()
                .title("한국 전력 공사 IT 대외활동")
                .organization("한국전력공사")
                .corporateType("중소기업")
                .participate("대학생")
                .awardScale("기타, 상장 수여")
                .startDate(LocalDate.of(2023, 1, 2))
                .endDate(LocalDate.of(2023, 2, 3))
                .homepageUrl("https://blog.naver.com/gyeryongcity1/222972814128")
                .activityBenefit("실무 교육, 수료증 및 인증서")
                .bonusBenefit("훈련장려금 지급")
                .description("""
                    🔥전액 무료🔥 딱 한 달만에
                    최신 '반도체 실무 스킬' 쌓는 법!
                    <반도체 실무 온라인 실습과정 : 소자+핵심공정> 강의
                    """)
                .imageUrl("“https://images/urls/1”")
                .build()
        );

        // when, then
        mockMvc.perform(get("/api/v2/competitions/{competitionId}", 1L))
            .andExpect(status().isOk())
            .andDo(document("competition-get",
                preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint()),
                pathParameters(
                    parameterWithName("competitionId").description("공모전 식별자").optional()),
                responseFields(
                    fieldWithPath("code").type(JsonFieldType.NUMBER).description("응답 상태코드"),
                    fieldWithPath("message").type(JsonFieldType.STRING).description("메시지"),
                    fieldWithPath("data").type(JsonFieldType.OBJECT).description("응답데이터"),
                    fieldWithPath("data.title").type(JsonFieldType.STRING)
                        .description("공모전 제목"),
                    fieldWithPath("data.organization").type(JsonFieldType.STRING)
                        .description("공모전 주최"),
                    fieldWithPath("data.corporateType").type(JsonFieldType.STRING)
                        .description("공모전 주최 기업 형태"),
                    fieldWithPath("data.participate").type(JsonFieldType.STRING)
                        .description("공모전 참여 대상"),
                    fieldWithPath("data.awardScale").type(JsonFieldType.STRING)
                        .description("공모전 시상 규모"),
                    fieldWithPath("data.startDate").type(JsonFieldType.STRING)
                        .description("공모전 접수 시작일"),
                    fieldWithPath("data.endDate").type(JsonFieldType.STRING)
                        .description("공모전 접수 마감일"),
                    fieldWithPath("data.homepageUrl").type(JsonFieldType.STRING)
                        .description("공모전 홈페이지 URL"),
                    fieldWithPath("data.activityBenefit").type(JsonFieldType.STRING)
                        .description("공모전 활동 혜택"),
                    fieldWithPath("data.bonusBenefit").type(JsonFieldType.STRING)
                        .description("공모전 추가 혜택"),
                    fieldWithPath("data.description").type(JsonFieldType.STRING)
                        .description("공모전 상세 내용"),
                    fieldWithPath("data.imageUrl").type(JsonFieldType.STRING)
                        .description("공모전 이미지 URL"))));
    }
}
