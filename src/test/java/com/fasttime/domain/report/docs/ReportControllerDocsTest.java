package com.fasttime.domain.report.docs;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
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
import com.fasttime.domain.report.dto.ReportDTO;
import com.fasttime.domain.report.dto.request.CreateReportRequest;
import com.fasttime.domain.report.service.ReportService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.restdocs.constraints.ConstraintDescriptions;
import org.springframework.restdocs.payload.JsonFieldType;

public class ReportControllerDocsTest extends RestDocsSupport {

    private final ReportService reportService = mock(ReportService.class);

    @Override
    public Object initController() {
        return new ReportRestController(reportService);
    }

    ConstraintDescriptions createReportRequestConstraints = new ConstraintDescriptions(
        CreateReportRequest.class);

    @DisplayName("신고 등록 API 문서화")
    @Test
    void createReport() throws Exception {
        // given
        CreateReportRequest request = CreateReportRequest.builder().postId(0L).memberId(0L).build();
        ReportDTO reportDTO = ReportDTO.builder().id(0L).postId(0L).memberId(0L).build();
        given(reportService.createReport(any(CreateReportRequest.class))).willReturn(reportDTO);
        String json = new ObjectMapper().writeValueAsString(request);

        // when, then
        mockMvc.perform(
                post("/api/v1/report/create").content(json).contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isCreated()).andDo(
                document("report-create", preprocessRequest(prettyPrint()),
                    preprocessResponse(prettyPrint()), requestFields(
                        fieldWithPath("postId").type(JsonFieldType.NUMBER).description("게시글 식별자")
                            .attributes(key("constraints").value(
                                createReportRequestConstraints.descriptionsForProperty("postId"))),
                        fieldWithPath("memberId").type(JsonFieldType.NUMBER).description("회원 식별자")
                            .attributes(key("constraints").value(
                                createReportRequestConstraints.descriptionsForProperty("memberId")))),
                    responseFields(
                        fieldWithPath("code").type(JsonFieldType.NUMBER).description("응답 상태코드"),
                        fieldWithPath("message").type(JsonFieldType.STRING).description("메시지"),
                        fieldWithPath("data").type(JsonFieldType.OBJECT).description("응답데이터"),
                        fieldWithPath("data.id").type(JsonFieldType.NUMBER).description("신고 식별자"),
                        fieldWithPath("data.postId").type(JsonFieldType.NUMBER).description("게시글 식별자"),
                        fieldWithPath("data.memberId").type(JsonFieldType.NUMBER)
                            .description("회원 식별자"))));
    }
}
