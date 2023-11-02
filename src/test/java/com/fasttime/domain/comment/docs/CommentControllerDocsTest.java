package com.fasttime.domain.comment.docs;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.patch;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.requestParameters;
import static org.springframework.restdocs.snippet.Attributes.key;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasttime.docs.RestDocsSupport;
import com.fasttime.domain.comment.controller.CommentRestController;
import com.fasttime.domain.comment.dto.request.CreateCommentRequestDTO;
import com.fasttime.domain.comment.dto.request.DeleteCommentRequestDTO;
import com.fasttime.domain.comment.dto.request.GetCommentsRequestDTO;
import com.fasttime.domain.comment.dto.request.UpdateCommentRequestDTO;
import com.fasttime.domain.comment.dto.response.CommentResponseDTO;
import com.fasttime.domain.comment.service.CommentService;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.restdocs.constraints.ConstraintDescriptions;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

public class CommentControllerDocsTest extends RestDocsSupport {

    private final CommentService commentService = mock(CommentService.class);

    @Override
    public Object initController() {
        return new CommentRestController(commentService);
    }

    ConstraintDescriptions createCommentRequestConstraints = new ConstraintDescriptions(
        CreateCommentRequestDTO.class);
    ConstraintDescriptions updateCommentRequestConstraints = new ConstraintDescriptions(
        UpdateCommentRequestDTO.class);
    ConstraintDescriptions deleteCommentRequestConstraints = new ConstraintDescriptions(
        DeleteCommentRequestDTO.class);

    @DisplayName("댓글 등록 API 문서화")
    @Test
    void createComment() throws Exception {
        // given
        CreateCommentRequestDTO request = CreateCommentRequestDTO.builder().postId(1L)
            .content("test").anonymity(false).parentCommentId(null).build();
        given(commentService.createComment(any(CreateCommentRequestDTO.class),
            any(Long.class))).willReturn(
            CommentResponseDTO.builder().commentId(1L).articleId(1L).memberId(1L)
                .nickname("nickname1").content("이거 왜 이럴까요?").anonymity(false).parentCommentId(-1L)
                .createdAt("2023-10-01 12:01:23").updatedAt(null).deletedAt(null)
                .childCommentCount(0).build());
        String json = new ObjectMapper().writeValueAsString(request);
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("MEMBER", 1L);

        // when, then
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/comments").content(json)
                .contentType(MediaType.APPLICATION_JSON).session(session))
            .andExpect(status().isCreated()).andDo(
                document("comment-create", preprocessRequest(prettyPrint()),
                    preprocessResponse(prettyPrint()), requestFields(
                        fieldWithPath("postId").type(JsonFieldType.NUMBER).description("게시글 식별자")
                            .attributes(key("constraints").value(
                                createCommentRequestConstraints.descriptionsForProperty("postId"))),
                        fieldWithPath("content").type(JsonFieldType.STRING).description("내용")
                            .attributes(key("constraints").value(
                                createCommentRequestConstraints.descriptionsForProperty("content"))),
                        fieldWithPath("anonymity").type(JsonFieldType.BOOLEAN).description("익명여부")
                            .attributes(key("constraints").value(
                                createCommentRequestConstraints.descriptionsForProperty("anonymity"))),
                        fieldWithPath("parentCommentId").type(JsonFieldType.NUMBER).optional()
                            .description("부모 댓글 식별자")), responseFields(
                        fieldWithPath("code").type(JsonFieldType.NUMBER).description("응답 상태코드"),
                        fieldWithPath("message").type(JsonFieldType.STRING).description("메시지"),
                        fieldWithPath("data").type(JsonFieldType.OBJECT).description("응답데이터"),
                        fieldWithPath("data.commentId").type(JsonFieldType.NUMBER)
                            .description("댓글 식별자"),
                        fieldWithPath("data.articleId").type(JsonFieldType.NUMBER)
                            .description("게시글 식별자"),
                        fieldWithPath("data.memberId").type(JsonFieldType.NUMBER).description("회원 식별자"),
                        fieldWithPath("data.nickname").type(JsonFieldType.STRING)
                            .description("작성자 닉네임"),
                        fieldWithPath("data.content").type(JsonFieldType.STRING).description("댓글 내용"),
                        fieldWithPath("data.anonymity").type(JsonFieldType.BOOLEAN)
                            .description("익명 여부"),
                        fieldWithPath("data.parentCommentId").type(JsonFieldType.NUMBER)
                            .description("부모 댓글 식별자"),
                        fieldWithPath("data.createdAt").type(JsonFieldType.STRING).description("등록 일시"),
                        fieldWithPath("data.updatedAt").type(JsonFieldType.STRING).optional()
                            .description("수정 일시"),
                        fieldWithPath("data.deletedAt").type(JsonFieldType.STRING).optional()
                            .description("삭제 일시"),
                        fieldWithPath("data.childCommentCount").type(JsonFieldType.NUMBER)
                            .description("대댓글 개수"))));
    }

    @DisplayName("댓글 목록 조회 API 문서화")
    @Test
    void getComments() throws Exception {
        // given
        when(commentService.getComments(any(GetCommentsRequestDTO.class))).thenReturn(List.of(
            CommentResponseDTO.builder().commentId(1L).articleId(1L).memberId(1L)
                .nickname("nickname1").content("이거 왜 이럴까요?").anonymity(false).parentCommentId(-1L)
                .createdAt("2023-10-01 12:01:23").updatedAt(null).deletedAt(null)
                .childCommentCount(2).build(),
            CommentResponseDTO.builder().commentId(3L).articleId(1L).memberId(3L)
                .nickname("nickname3").content("오...").anonymity(false).parentCommentId(-1L)
                .createdAt("2023-10-01 16:14:50").updatedAt(null).deletedAt(null)
                .childCommentCount(0).build(),
            CommentResponseDTO.builder().commentId(4L).articleId(1L).memberId(3L)
                .nickname("nickname3").content("굿").anonymity(false).parentCommentId(-1L)
                .createdAt("2023-10-01 20:01:45").updatedAt(null).deletedAt(null)
                .childCommentCount(1).build()));

        // when, then
        mockMvc.perform(get("/api/v1/comments").queryParam("articleId", "1").queryParam("page", "0")
            .queryParam("pageSize", "10")).andExpect(status().isOk()).andDo(
            document("comments-search", preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                requestParameters(parameterWithName("articleId").description("게시글 식별자").optional(),
                    parameterWithName("memberId").description("회원 식별자").optional(),
                    parameterWithName("parentCommentId").description("대댓글을 조회할 댓글 식별자").optional(),
                    parameterWithName("pageSize").description("조회당 불러올 건 수").optional(),
                    parameterWithName("page").description("조회 페이지").optional()), responseFields(
                    fieldWithPath("code").type(JsonFieldType.NUMBER).description("응답 상태코드"),
                    fieldWithPath("message").type(JsonFieldType.STRING).description("메시지"),
                    fieldWithPath("data").type(JsonFieldType.ARRAY).description("응답데이터"),
                    fieldWithPath("data[].commentId").type(JsonFieldType.NUMBER)
                        .description("댓글 식별자"),
                    fieldWithPath("data[].articleId").type(JsonFieldType.NUMBER)
                        .description("게시글 식별자"),
                    fieldWithPath("data[].memberId").type(JsonFieldType.NUMBER)
                        .description("회원 식별자"),
                    fieldWithPath("data[].nickname").type(JsonFieldType.STRING)
                        .description("작성자 닉네임"),
                    fieldWithPath("data[].content").type(JsonFieldType.STRING).description("댓글 내용"),
                    fieldWithPath("data[].anonymity").type(JsonFieldType.BOOLEAN)
                        .description("익명 여부"),
                    fieldWithPath("data[].parentCommentId").type(JsonFieldType.NUMBER)
                        .description("부모 댓글 식별자"),
                    fieldWithPath("data[].createdAt").type(JsonFieldType.STRING)
                        .description("등록 일시"),
                    fieldWithPath("data[].updatedAt").type(JsonFieldType.STRING).optional()
                        .description("수정 일시"),
                    fieldWithPath("data[].deletedAt").type(JsonFieldType.STRING).optional()
                        .description("삭제 일시"),
                    fieldWithPath("data[].childCommentCount").type(JsonFieldType.NUMBER)
                        .description("대댓글 개수"))));
    }

    @DisplayName("게시글 수정 API 문서화")
    @Test
    void postUpdate() throws Exception {
        // given
        UpdateCommentRequestDTO request = UpdateCommentRequestDTO.builder().id(1L)
            .content("modified").build();
        given(commentService.updateComment(any(UpdateCommentRequestDTO.class))).willReturn(
            CommentResponseDTO.builder().commentId(1L).articleId(1L).memberId(1L)
                .nickname("nickname1").content("이거 왜 이럴까요?").anonymity(false).parentCommentId(-1L)
                .createdAt("2023-10-01 12:01:23").updatedAt("2023-10-01 13:00:07").deletedAt(null)
                .childCommentCount(2).build());
        String json = new ObjectMapper().writeValueAsString(request);

        // when, then
        mockMvc.perform(
                patch("/api/v1/comments").contentType(MediaType.APPLICATION_JSON).content(json))
            .andExpect(status().isOk()).andDo(
                document("comment-update", preprocessRequest(prettyPrint()),
                    preprocessResponse(prettyPrint()), requestFields(
                        fieldWithPath("id").type(JsonFieldType.NUMBER).description("댓글 식별자").attributes(
                            key("constraints").value(
                                updateCommentRequestConstraints.descriptionsForProperty("id"))),
                        fieldWithPath("content").type(JsonFieldType.STRING).description("내용")
                            .attributes(key("constraints").value(
                                updateCommentRequestConstraints.descriptionsForProperty("content")))),
                    responseFields(
                        fieldWithPath("code").type(JsonFieldType.NUMBER).description("응답 상태코드"),
                        fieldWithPath("message").type(JsonFieldType.STRING).description("메시지"),
                        fieldWithPath("data").type(JsonFieldType.OBJECT).description("응답데이터"),
                        fieldWithPath("data.commentId").type(JsonFieldType.NUMBER)
                            .description("댓글 식별자"),
                        fieldWithPath("data.articleId").type(JsonFieldType.NUMBER)
                            .description("게시글 식별자"),
                        fieldWithPath("data.memberId").type(JsonFieldType.NUMBER).description("회원 식별자"),
                        fieldWithPath("data.nickname").type(JsonFieldType.STRING)
                            .description("작성자 닉네임"),
                        fieldWithPath("data.content").type(JsonFieldType.STRING).description("댓글 내용"),
                        fieldWithPath("data.anonymity").type(JsonFieldType.BOOLEAN)
                            .description("익명 여부"),
                        fieldWithPath("data.parentCommentId").type(JsonFieldType.NUMBER)
                            .description("부모 댓글 식별자"),
                        fieldWithPath("data.createdAt").type(JsonFieldType.STRING).description("등록 일시"),
                        fieldWithPath("data.updatedAt").type(JsonFieldType.STRING)
                            .description("수정 일시"),
                        fieldWithPath("data.deletedAt").type(JsonFieldType.STRING).optional()
                            .description("삭제 일시"),
                        fieldWithPath("data.childCommentCount").type(JsonFieldType.NUMBER)
                            .description("대댓글 개수"))));
    }

    @DisplayName("게시글 삭제 API 문서화")
    @Test
    void postDelete() throws Exception {
        // given
        DeleteCommentRequestDTO request = DeleteCommentRequestDTO.builder().id(0L).build();
        String json = new ObjectMapper().writeValueAsString(request);
        given(commentService.deleteComment(any(DeleteCommentRequestDTO.class))).willReturn(
            CommentResponseDTO.builder().commentId(1L).articleId(1L).memberId(1L)
                .nickname("nickname1").content("이거 왜 이럴까요?").anonymity(false).parentCommentId(-1L)
                .createdAt("2023-10-01 12:01:23").updatedAt("2023-10-01 13:00:07")
                .deletedAt("2023-10-03 21:17:03").childCommentCount(2).build());

        // when, then
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/comments").content(json)
            .contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk()).andDo(
            document("comment-delete", preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()), requestFields(
                    fieldWithPath("id").type(JsonFieldType.NUMBER).description("댓글 식별자").attributes(
                        key("constraints").value(
                            deleteCommentRequestConstraints.descriptionsForProperty("id")))),
                responseFields(
                    fieldWithPath("code").type(JsonFieldType.NUMBER).description("응답 상태코드"),
                    fieldWithPath("message").type(JsonFieldType.STRING).description("메시지"),
                    fieldWithPath("data").type(JsonFieldType.OBJECT).description("응답데이터"),
                    fieldWithPath("data.commentId").type(JsonFieldType.NUMBER)
                        .description("댓글 식별자"),
                    fieldWithPath("data.articleId").type(JsonFieldType.NUMBER)
                        .description("게시글 식별자"),
                    fieldWithPath("data.memberId").type(JsonFieldType.NUMBER).description("회원 식별자"),
                    fieldWithPath("data.nickname").type(JsonFieldType.STRING)
                        .description("작성자 닉네임"),
                    fieldWithPath("data.content").type(JsonFieldType.STRING).description("댓글 내용"),
                    fieldWithPath("data.anonymity").type(JsonFieldType.BOOLEAN)
                        .description("익명 여부"),
                    fieldWithPath("data.parentCommentId").type(JsonFieldType.NUMBER)
                        .description("부모 댓글 식별자"),
                    fieldWithPath("data.createdAt").type(JsonFieldType.STRING).description("등록 일시"),
                    fieldWithPath("data.updatedAt").type(JsonFieldType.STRING)
                        .description("수정 일시"),
                    fieldWithPath("data.deletedAt").type(JsonFieldType.STRING)
                        .description("삭제 일시"),
                    fieldWithPath("data.childCommentCount").type(JsonFieldType.NUMBER)
                        .description("대댓글 개수"))));
    }
}
