package com.fasttime.domain.record.docs;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.snippet.Attributes.key;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasttime.docs.RestDocsSupport;
import com.fasttime.domain.record.controller.RecordRestController;
import com.fasttime.domain.record.dto.RecordDTO;
import com.fasttime.domain.record.dto.request.CreateRecordRequestDTO;
import com.fasttime.domain.record.dto.request.DeleteRecordRequestDTO;
import com.fasttime.domain.record.service.RecordService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.restdocs.constraints.ConstraintDescriptions;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

public class RecordControllerDocsTest extends RestDocsSupport {

    private final RecordService recordService = mock(RecordService.class);

    @Override
    public Object initController() {
        return new RecordRestController(recordService);
    }

    ConstraintDescriptions createRecordRequestConstraints = new ConstraintDescriptions(
        CreateRecordRequestDTO.class);
    ConstraintDescriptions deleteRecordRequestConstraints = new ConstraintDescriptions(
        DeleteRecordRequestDTO.class);

    @DisplayName("좋아요 등록 API 문서화")
    @Test
    void createLike() throws Exception {
        // given
        CreateRecordRequestDTO request = CreateRecordRequestDTO.builder().memberId(1L).postId(1L)
            .build();
        doNothing().when(recordService)
            .createRecord(any(CreateRecordRequestDTO.class), anyBoolean());
        String json = new ObjectMapper().writeValueAsString(request);

        // when, then
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/record/like").content(json)
            .contentType(MediaType.APPLICATION_JSON)).andExpect(status().isCreated()).andDo(
            document("record-like-create", preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()), requestFields(
                    fieldWithPath("memberId").type(JsonFieldType.NUMBER).description("회원 식별자")
                        .attributes(key("constraints").value(
                            createRecordRequestConstraints.descriptionsForProperty("memberId"))),
                    fieldWithPath("postId").type(JsonFieldType.NUMBER).description("게시글 식별자")
                        .attributes(key("constraints").value(
                            createRecordRequestConstraints.descriptionsForProperty("postId")))),
                responseFields(
                    fieldWithPath("code").type(JsonFieldType.NUMBER).description("응답 상태코드"),
                    fieldWithPath("message").type(JsonFieldType.STRING).description("메시지"),
                    fieldWithPath("data").type(JsonFieldType.NULL).description("응답데이터"))));
    }

    @DisplayName("싫어요 등록 API 문서화")
    @Test
    void createHate() throws Exception {
        // given
        CreateRecordRequestDTO request = CreateRecordRequestDTO.builder().memberId(1L).postId(1L)
            .build();
        doNothing().when(recordService)
            .createRecord(any(CreateRecordRequestDTO.class), anyBoolean());
        String json = new ObjectMapper().writeValueAsString(request);

        // when, then
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/record/hate").content(json)
            .contentType(MediaType.APPLICATION_JSON)).andExpect(status().isCreated()).andDo(
            document("record-hate-create", preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()), requestFields(
                    fieldWithPath("memberId").type(JsonFieldType.NUMBER).description("회원 식별자")
                        .attributes(key("constraints").value(
                            createRecordRequestConstraints.descriptionsForProperty("memberId"))),
                    fieldWithPath("postId").type(JsonFieldType.NUMBER).description("게시글 식별자")
                        .attributes(key("constraints").value(
                            createRecordRequestConstraints.descriptionsForProperty("postId")))),
                responseFields(
                    fieldWithPath("code").type(JsonFieldType.NUMBER).description("응답 상태코드"),
                    fieldWithPath("message").type(JsonFieldType.STRING).description("메시지"),
                    fieldWithPath("data").type(JsonFieldType.NULL).description("응답데이터"))));
    }

    @DisplayName("좋아요/싫어요 조회 API 문서화")
    @Test
    void getRecord() throws Exception {
        // given
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("MEMBER", 1L);
        RecordDTO recordDTO = RecordDTO.builder().id(1L).memberId(1L).postId(1L).isLike(true)
            .build();
        given(recordService.getRecord(any(Long.class), any(Long.class))).willReturn(recordDTO);

        // when, then
        mockMvc.perform(
                RestDocumentationRequestBuilders.get("/api/v1/record/{postId}", 1L).session(session))
            .andExpect(status().isOk()).andDo(
                document("record-get", preprocessRequest(prettyPrint()),
                    preprocessResponse(prettyPrint()),
                    pathParameters(parameterWithName("postId").description("게시글 식별자")), responseFields(
                        fieldWithPath("code").type(JsonFieldType.NUMBER).description("응답 상태코드"),
                        fieldWithPath("message").type(JsonFieldType.STRING).description("메시지"),
                        fieldWithPath("data").type(JsonFieldType.OBJECT).description("응답데이터"),
                        fieldWithPath("data.id").type(JsonFieldType.NUMBER).optional()
                            .description("좋아요/싫어요 식별자"),
                        fieldWithPath("data.memberId").type(JsonFieldType.NUMBER).optional()
                            .description("회원 식별자"),
                        fieldWithPath("data.postId").type(JsonFieldType.NUMBER).optional()
                            .description("게시글 식별자"),
                        fieldWithPath("data.isLike").type(JsonFieldType.BOOLEAN)
                            .description("좋아요(1), 싫어요(0)"))));
    }

    @DisplayName("좋아요/싫어요 취소 API 문서화")
    @Test
    void deleteRecord() throws Exception {
        // given
        DeleteRecordRequestDTO request = DeleteRecordRequestDTO.builder().memberId(1L).postId(1L)
            .build();
        doNothing().when(recordService).deleteRecord(any(DeleteRecordRequestDTO.class));
        String json = new ObjectMapper().writeValueAsString(request);

        // when, then
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/record").content(json)
            .contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk()).andDo(
            document("record-delete", preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()), requestFields(
                    fieldWithPath("memberId").type(JsonFieldType.NUMBER).description("회원 식별자")
                        .attributes(key("constraints").value(
                            deleteRecordRequestConstraints.descriptionsForProperty("memberId"))),
                    fieldWithPath("postId").type(JsonFieldType.NUMBER).description("게시글 식별자")
                        .attributes(key("constraints").value(
                            deleteRecordRequestConstraints.descriptionsForProperty("postId")))),
                responseFields(
                    fieldWithPath("code").type(JsonFieldType.NUMBER).description("응답 상태코드"),
                    fieldWithPath("message").type(JsonFieldType.STRING).description("메시지"),
                    fieldWithPath("data").type(JsonFieldType.NULL).description("응답데이터"))));
    }
}
