package com.fasttime.domain.memberArticleLike.docs;

import static org.mockito.ArgumentMatchers.any;
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
import com.fasttime.domain.memberArticleLike.controller.MemberArticleLikeRestController;
import com.fasttime.domain.memberArticleLike.dto.MemberArticleLikeDTO;
import com.fasttime.domain.memberArticleLike.dto.request.CreateMemberArticleLikeRequestDTO;
import com.fasttime.domain.memberArticleLike.dto.request.DeleteMemberArticleLikeRequestDTO;
import com.fasttime.domain.memberArticleLike.service.MemberArticleLikeService;
import com.fasttime.global.util.SecurityUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.restdocs.constraints.ConstraintDescriptions;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

class MemberArticleLikeControllerDocsTest extends RestDocsSupport {

    private final MemberArticleLikeService memberArticleLikeService = mock(
        MemberArticleLikeService.class);
    private final SecurityUtil securityUtil = mock(SecurityUtil.class);

    @Override
    public Object initController() {
        return new MemberArticleLikeRestController(memberArticleLikeService, securityUtil);
    }

    ConstraintDescriptions createRecordRequestConstraints = new ConstraintDescriptions(
        CreateMemberArticleLikeRequestDTO.class);
    ConstraintDescriptions deleteRecordRequestConstraints = new ConstraintDescriptions(
        DeleteMemberArticleLikeRequestDTO.class);

    @DisplayName("좋아요/싫어요 등록 API 문서화")
    @Test
    void createLike() throws Exception {
        // given
        CreateMemberArticleLikeRequestDTO request = CreateMemberArticleLikeRequestDTO.builder()
            .articleId(1L).isLike(true)
            .build();
        doNothing().when(memberArticleLikeService)
            .createMemberArticleLike(any(CreateMemberArticleLikeRequestDTO.class), any(Long.class));
        String json = new ObjectMapper().writeValueAsString(request);
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("MEMBER", 1L);

        // when, then
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/article-like").content(json)
                .contentType(MediaType.APPLICATION_JSON).session(session))
            .andExpect(status().isCreated()).andDo(
                document("memberArticleLike-create", preprocessRequest(prettyPrint()),
                    preprocessResponse(prettyPrint()), requestFields(
                        fieldWithPath("articleId").type(JsonFieldType.NUMBER).description("게시글 식별자")
                            .attributes(key("constraints").value(
                                createRecordRequestConstraints.descriptionsForProperty("articleId"))),
                        fieldWithPath("isLike").type(JsonFieldType.BOOLEAN)
                            .description("좋아요(true)/싫어요(false)").attributes(key("constraints").value(
                                createRecordRequestConstraints.descriptionsForProperty("isLike")))),
                    responseFields(
                        fieldWithPath("code").type(JsonFieldType.NUMBER).description("응답 상태코드"),
                        fieldWithPath("message").type(JsonFieldType.STRING).description("메시지"),
                        fieldWithPath("data").type(JsonFieldType.NULL).description("응답데이터"))));
    }

    @DisplayName("좋아요/싫어요 조회 API 문서화")
    @Test
    void getRecord() throws Exception {
        // given
        MemberArticleLikeDTO memberArticleLikeDTO = MemberArticleLikeDTO.builder().id(1L)
            .memberId(1L).articleId(1L).isLike(true)
            .build();
        given(memberArticleLikeService.getMemberArticleLike(any(Long.class),
            any(Long.class))).willReturn(
            memberArticleLikeDTO);
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("MEMBER", 1L);

        // when, then
        mockMvc.perform(
                RestDocumentationRequestBuilders.get("/api/v1/article-like/{articleId}", 1L).session(session))
            .andExpect(status().isOk()).andDo(
                document("memberArticleLike-get", preprocessRequest(prettyPrint()),
                    preprocessResponse(prettyPrint()),
                    pathParameters(parameterWithName("articleId").description("게시글 식별자")),
                    responseFields(
                        fieldWithPath("code").type(JsonFieldType.NUMBER).description("응답 상태코드"),
                        fieldWithPath("message").type(JsonFieldType.STRING).description("메시지"),
                        fieldWithPath("data").type(JsonFieldType.OBJECT).description("응답데이터"),
                        fieldWithPath("data.id").type(JsonFieldType.NUMBER).optional()
                            .description("좋아요/싫어요 식별자"),
                        fieldWithPath("data.memberId").type(JsonFieldType.NUMBER).optional()
                            .description("회원 식별자"),
                        fieldWithPath("data.articleId").type(JsonFieldType.NUMBER).optional()
                            .description("게시글 식별자"),
                        fieldWithPath("data.isLike").type(JsonFieldType.BOOLEAN).optional()
                            .description("좋아요(true)/싫어요(false)"))));
    }

    @DisplayName("좋아요/싫어요 취소 API 문서화")
    @Test
    void deleteRecord() throws Exception {
        // given
        DeleteMemberArticleLikeRequestDTO request = DeleteMemberArticleLikeRequestDTO.builder()
            .articleId(1L).build();
        doNothing().when(memberArticleLikeService)
            .deleteMemberArticleLike(any(DeleteMemberArticleLikeRequestDTO.class), any(Long.class));
        String json = new ObjectMapper().writeValueAsString(request);

        // when, then
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/article-like").content(json)
            .contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk()).andDo(
            document("memberArticleLike-delete", preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()), requestFields(
                    fieldWithPath("articleId").type(JsonFieldType.NUMBER).description("게시글 식별자")
                        .attributes(key("constraints").value(
                            deleteRecordRequestConstraints.descriptionsForProperty("articleId")))),
                responseFields(
                    fieldWithPath("code").type(JsonFieldType.NUMBER).description("응답 상태코드"),
                    fieldWithPath("message").type(JsonFieldType.STRING).description("메시지"),
                    fieldWithPath("data").type(JsonFieldType.NULL).description("응답데이터"))));
    }
}
