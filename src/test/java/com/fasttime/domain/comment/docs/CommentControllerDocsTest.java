package com.fasttime.domain.comment.docs;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasttime.docs.RestDocsSupport;
import com.fasttime.domain.comment.controller.CommentRestController;
import com.fasttime.domain.comment.dto.request.CreateCommentRequest;
import com.fasttime.domain.comment.dto.request.DeleteCommentRequest;
import com.fasttime.domain.comment.dto.request.UpdateCommentRequest;
import com.fasttime.domain.comment.dto.response.MyPageCommentResponseDTO;
import com.fasttime.domain.comment.dto.response.PostCommentResponseDTO;
import com.fasttime.domain.comment.service.CommentService;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

public class CommentControllerDocsTest extends RestDocsSupport {

    private final CommentService commentService = mock(CommentService.class);

    @Override
    public Object initController() {
        return new CommentRestController(commentService);
    }

    @DisplayName("댓글 등록 API 문서화")
    @Test
    void createComment() throws Exception {
        // given
        CreateCommentRequest request = CreateCommentRequest.builder().postId(1L).memberId(1L)
            .content("test").anonymity(false).parentCommentId(null).build();
        PostCommentResponseDTO postCommentResponseDto = PostCommentResponseDTO.builder().id(1L)
            .memberId(1L).nickname("nickname").content("test").anonymity(false)
            .parentCommentId(null).createdAt("2023-01-01 12:30:00").updatedAt(null).deletedAt(null)
            .build();
        given(commentService.createComment(any(CreateCommentRequest.class))).willReturn(
            postCommentResponseDto);
        String json = new ObjectMapper().writeValueAsString(request);

        // when, then
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/comment").content(json)
            .contentType(MediaType.APPLICATION_JSON)).andExpect(status().isCreated()).andDo(
            document("comment-create", preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()), requestFields(
                    fieldWithPath("postId").type(JsonFieldType.NUMBER).description("게시글 식별자"),
                    fieldWithPath("memberId").type(JsonFieldType.NUMBER).description("회원 식별자"),
                    fieldWithPath("content").type(JsonFieldType.STRING).description("내용"),
                    fieldWithPath("anonymity").type(JsonFieldType.BOOLEAN).description("익명여부"),
                    fieldWithPath("parentCommentId").type(JsonFieldType.NUMBER).optional()
                        .description("부모 댓글 식별자")), responseFields(
                    fieldWithPath("code").type(JsonFieldType.NUMBER).description("응답 상태코드"),
                    fieldWithPath("message").type(JsonFieldType.STRING).description("메시지"),
                    fieldWithPath("data").type(JsonFieldType.OBJECT).description("응답데이터"),
                    fieldWithPath("data.id").type(JsonFieldType.NUMBER).description("댓글 식별자"),
                    fieldWithPath("data.memberId").type(JsonFieldType.NUMBER).description("회원 식별자"),
                    fieldWithPath("data.nickname").type(JsonFieldType.STRING)
                        .description("작성자 닉네임"),
                    fieldWithPath("data.content").type(JsonFieldType.STRING).description("댓글 내용"),
                    fieldWithPath("data.anonymity").type(JsonFieldType.BOOLEAN)
                        .description("익명 여부"),
                    fieldWithPath("data.parentCommentId").type(JsonFieldType.NUMBER).optional()
                        .description("부모 댓글 식별자"),
                    fieldWithPath("data.createdAt").type(JsonFieldType.STRING).description("등록 일시"),
                    fieldWithPath("data.updatedAt").type(JsonFieldType.STRING).optional()
                        .description("수정 일시"),
                    fieldWithPath("data.deletedAt").type(JsonFieldType.STRING).optional()
                        .description("삭제 일시"))));
    }

    @DisplayName("마이 페이지 댓글 목록 조회 API 문서화")
    @Test
    void getCommentsByMemberId() throws Exception {
        // given
        when(commentService.getCommentsByMemberId(any(Long.class))).thenReturn(List.of(
            MyPageCommentResponseDTO.builder().id(1L).postId(1L).nickname("nickname1")
                .content("좋은 글 잘 보고 갑니다.").anonymity(true).parentCommentId(null)
                .createdAt("2023-01-01 12:30:00").updatedAt(null).deletedAt(null).build(),
            MyPageCommentResponseDTO.builder().id(2L).postId(2L).nickname("nickname1")
                .content("이유가 뭔가요?").anonymity(true).parentCommentId(1L)
                .createdAt("2023-01-01 12:35:00").updatedAt(null).deletedAt(null).build(),
            MyPageCommentResponseDTO.builder().id(3L).postId(2L).nickname("nickname1")
                .content("아하!").anonymity(true).parentCommentId(1L).createdAt("2023-01-01 12:37:00")
                .updatedAt(null).deletedAt(null).build()));

        // when, then
        mockMvc.perform(get("/api/v1/comment/my-page/{memberId}", 1)).andExpect(status().isOk())
            .andDo(document("comments-search-mypage", preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()), responseFields(
                    fieldWithPath("code").type(JsonFieldType.NUMBER).description("응답 상태코드"),
                    fieldWithPath("message").type(JsonFieldType.STRING).description("메시지"),
                    fieldWithPath("data").type(JsonFieldType.ARRAY).description("응답데이터"),
                    fieldWithPath("data[].id").type(JsonFieldType.NUMBER).description("댓글 식별자"),
                    fieldWithPath("data[].postId").type(JsonFieldType.NUMBER)
                        .description("게시글 식별자"),
                    fieldWithPath("data[].nickname").type(JsonFieldType.STRING)
                        .description("작성자 닉네임"),
                    fieldWithPath("data[].content").type(JsonFieldType.STRING).description("댓글 내용"),
                    fieldWithPath("data[].anonymity").type(JsonFieldType.BOOLEAN)
                        .description("익명 여부"),
                    fieldWithPath("data[].parentCommentId").type(JsonFieldType.NUMBER).optional()
                        .description("부모 댓글 식별자"),
                    fieldWithPath("data[].createdAt").type(JsonFieldType.STRING)
                        .description("등록 일시"),
                    fieldWithPath("data[].updatedAt").type(JsonFieldType.STRING).optional()
                        .description("수정 일시"),
                    fieldWithPath("data[].deletedAt").type(JsonFieldType.STRING).optional()
                        .description("삭제 일시"))));
    }

    @DisplayName("게시글 상세 페이지 댓글 목록 조회 API 문서화")
    @Test
    void getCommentsByPostId() throws Exception {
        // given
        when(commentService.getCommentsByPostId(any(Long.class))).thenReturn(List.of(
            PostCommentResponseDTO.builder().id(1L).memberId(1L).nickname("nickname1")
                .content("공감합니다.").anonymity(false).parentCommentId(null)
                .createdAt("2023-01-01 12:30:00").updatedAt(null).deletedAt(null).build(),
            PostCommentResponseDTO.builder().id(2L).memberId(2L).nickname("nickname2")
                .content("저도요.").anonymity(true).parentCommentId(1L)
                .createdAt("2023-01-01 12:40:00").updatedAt(null).deletedAt(null).build(),
            PostCommentResponseDTO.builder().id(3L).memberId(1L).nickname("nickname1")
                .content("역시 다들 비슷하네요.").anonymity(false).parentCommentId(1L)
                .createdAt("2023-01-01 12:42:00").updatedAt(null).deletedAt(null).build()));

        // when, then
        mockMvc.perform(get("/api/v1/comment/{postId}", 1)).andExpect(status().isOk()).andDo(
            document("comments-search-post", preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()), responseFields(
                    fieldWithPath("code").type(JsonFieldType.NUMBER).description("응답 상태코드"),
                    fieldWithPath("message").type(JsonFieldType.STRING).description("메시지"),
                    fieldWithPath("data").type(JsonFieldType.ARRAY).description("응답데이터"),
                    fieldWithPath("data[].id").type(JsonFieldType.NUMBER).description("댓글 식별자"),
                    fieldWithPath("data[].memberId").type(JsonFieldType.NUMBER)
                        .description("회원 식별자"),
                    fieldWithPath("data[].nickname").type(JsonFieldType.STRING)
                        .description("작성자 닉네임"),
                    fieldWithPath("data[].content").type(JsonFieldType.STRING).description("댓글 내용"),
                    fieldWithPath("data[].anonymity").type(JsonFieldType.BOOLEAN)
                        .description("익명 여부"),
                    fieldWithPath("data[].parentCommentId").type(JsonFieldType.NUMBER).optional()
                        .description("부모 댓글 식별자"),
                    fieldWithPath("data[].createdAt").type(JsonFieldType.STRING)
                        .description("등록 일시"),
                    fieldWithPath("data[].updatedAt").type(JsonFieldType.STRING).optional()
                        .description("수정 일시"),
                    fieldWithPath("data[].deletedAt").type(JsonFieldType.STRING).optional()
                        .description("삭제 일시"))));
    }

    @DisplayName("게시글 수정 API 문서화")
    @Test
    void postUpdate() throws Exception {
        // given
        UpdateCommentRequest request = UpdateCommentRequest.builder().id(1L).content("modified")
            .build();
        PostCommentResponseDTO postCommentResponseDto = PostCommentResponseDTO.builder().id(2L)
            .memberId(2L).nickname("nickname2").content("감사합니다. -수정").anonymity(true)
            .parentCommentId(1L).createdAt("2023-01-01 12:40:00").updatedAt("2023-01-01 12:41:00")
            .deletedAt(null).build();
        given(commentService.updateComment(any(UpdateCommentRequest.class))).willReturn(
            postCommentResponseDto);
        String json = new ObjectMapper().writeValueAsString(request);

        // when, then
        mockMvc.perform(
                patch("/api/v1/comment").contentType(MediaType.APPLICATION_JSON).content(json))
            .andExpect(status().isOk()).andDo(
                document("comment-update", preprocessRequest(prettyPrint()),
                    preprocessResponse(prettyPrint()),
                    requestFields(fieldWithPath("id").type(JsonFieldType.NUMBER).description("댓글 식별자"),
                        fieldWithPath("content").type(JsonFieldType.STRING).description("내용")),
                    responseFields(
                        fieldWithPath("code").type(JsonFieldType.NUMBER).description("응답 상태코드"),
                        fieldWithPath("message").type(JsonFieldType.STRING).description("메시지"),
                        fieldWithPath("data").type(JsonFieldType.OBJECT).description("응답데이터"),
                        fieldWithPath("data.id").type(JsonFieldType.NUMBER).description("댓글 식별자"),
                        fieldWithPath("data.memberId").type(JsonFieldType.NUMBER).description("회원 식별자"),
                        fieldWithPath("data.nickname").type(JsonFieldType.STRING)
                            .description("작성자 닉네임"),
                        fieldWithPath("data.content").type(JsonFieldType.STRING).description("댓글 내용"),
                        fieldWithPath("data.anonymity").type(JsonFieldType.BOOLEAN)
                            .description("익명 여부"),
                        fieldWithPath("data.parentCommentId").type(JsonFieldType.NUMBER).optional()
                            .description("부모 댓글 식별자"),
                        fieldWithPath("data.createdAt").type(JsonFieldType.STRING).description("등록 일시"),
                        fieldWithPath("data.updatedAt").type(JsonFieldType.STRING).description("수정 일시"),
                        fieldWithPath("data.deletedAt").type(JsonFieldType.STRING).optional()
                            .description("삭제 일시"))));
    }

    @DisplayName("게시글 삭제 API 문서화")
    @Test
    void postDelete() throws Exception {
        // given
        DeleteCommentRequest request = DeleteCommentRequest.builder().id(0L).build();
        String json = new ObjectMapper().writeValueAsString(request);
        doNothing().when(commentService).deleteComment(any(DeleteCommentRequest.class));

        // when, then
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/comment").content(json)
            .contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk()).andDo(
            document("comment-delete", preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                requestFields(fieldWithPath("id").type(JsonFieldType.NUMBER).description("댓글 식별자")),
                responseFields(
                    fieldWithPath("code").type(JsonFieldType.NUMBER).description("응답 상태코드"),
                    fieldWithPath("message").type(JsonFieldType.STRING).description("메시지"),
                    fieldWithPath("data").type(JsonFieldType.NULL).description("응답데이터"))));
    }
}
