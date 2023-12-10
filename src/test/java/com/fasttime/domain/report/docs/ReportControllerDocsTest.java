package com.fasttime.domain.report.docs;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.snippet.Attributes.key;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasttime.docs.RestDocsSupport;
import com.fasttime.domain.report.contoller.ReportRestController;
import com.fasttime.domain.report.dto.request.CreateReportRequestDTO;
import com.fasttime.domain.report.service.ReportService;
import com.fasttime.global.util.SecurityUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.restdocs.constraints.ConstraintDescriptions;
import org.springframework.restdocs.payload.JsonFieldType;

class ReportControllerDocsTest extends RestDocsSupport {

    private final ReportService reportService = mock(ReportService.class);
    private final SecurityUtil securityUtil = mock(SecurityUtil.class);

    @Override
    public Object initController() {
        return new ReportRestController(reportService, securityUtil);
    }

    ConstraintDescriptions createReportRequestConstraints = new ConstraintDescriptions(
        CreateReportRequestDTO.class);

    @DisplayName("신고 등록 API 문서화")
    @Test
    void createReport() throws Exception {
        // given
        CreateReportRequestDTO request = CreateReportRequestDTO.builder().postId(1L).build();
        doNothing().when(reportService)
            .createReport(any(CreateReportRequestDTO.class), any(Long.class));
        String json = new ObjectMapper().writeValueAsString(request);
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("MEMBER", 1L);

        // when, then
        mockMvc.perform(
            post("/api/v1/report").content(json).contentType(MediaType.APPLICATION_JSON)
                .session(session)).andExpect(status().isCreated()).andDo(
            document("report-create", preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()), requestFields(
                    fieldWithPath("postId").type(JsonFieldType.NUMBER).description("게시글 식별자")
                        .attributes(key("constraints").value(
                            createReportRequestConstraints.descriptionsForProperty("postId")))),
                responseFields(
                    fieldWithPath("code").type(JsonFieldType.NUMBER).description("응답 상태 코드"),
                    fieldWithPath("message").type(JsonFieldType.STRING).description("메시지"),
                    fieldWithPath("data").type(JsonFieldType.NULL).description("응답 데이터"))));
    }
}
