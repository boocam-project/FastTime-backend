package com.fasttime.domain.comment.docs;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasttime.docs.RestDocsSupport;
import com.fasttime.domain.comment.controller.CommentRestController;
import com.fasttime.domain.comment.dto.request.CreateCommentRequestDTO;
import com.fasttime.domain.comment.dto.request.GetCommentsRequestDTO;
import com.fasttime.domain.comment.dto.request.UpdateCommentRequestDTO;
import com.fasttime.domain.comment.dto.response.CommentListResponseDTO;
import com.fasttime.domain.comment.dto.response.CommentResponseDTO;
import com.fasttime.domain.comment.service.CommentService;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.restdocs.constraints.ConstraintDescriptions;
import org.springframework.restdocs.payload.JsonFieldType;

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

    @DisplayName("댓글 등록 API 문서화")
    @Test
    void createComment() throws Exception {
        // given
        String content = objectMapper.writeValueAsString(CreateCommentRequestDTO.builder()
            .content("얼마나 걸리셨나요?")
            .anonymity(false)
            .parentCommentId(null)
            .build());
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("MEMBER", 1L);
        given(commentService.createComment(any(long.class), any(long.class),
            any(CreateCommentRequestDTO.class))).willReturn(
            CommentResponseDTO.builder()
                .commentId(1L)
                .articleId(1L)
                .memberId(1L)
                .nickname("깜찍이")
                .content("얼마나 걸리셨나요?")
                .anonymity(false)
                .parentCommentId(-1L)
                .childCommentCount(0)
                .createdAt("2024-01-01 12:00:00")
                .updatedAt(null)
                .deletedAt(null)
                .build());

        // when, then
        mockMvc.perform(post("/api/v1/comments/{articleId}", 1L)
                .session(session)
                .content(content)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isCreated())
            .andDo(document("comment-create",
                preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint()),
                requestFields(
                    fieldWithPath("content").type(JsonFieldType.STRING).description("내용")
                        .attributes(key("constraints").value(
                            createCommentRequestConstraints.descriptionsForProperty("content"))),
                    fieldWithPath("anonymity").type(JsonFieldType.BOOLEAN).description("익명여부")
                        .attributes(key("constraints").value(
                            createCommentRequestConstraints.descriptionsForProperty("anonymity"))),
                    fieldWithPath("parentCommentId").type(JsonFieldType.NUMBER)
                        .description("부모 댓글 식별자").optional()),
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
                    fieldWithPath("data.childCommentCount").type(JsonFieldType.NUMBER)
                        .description("대댓글 개수"),
                    fieldWithPath("data.createdAt").type(JsonFieldType.STRING).description("등록 일시"),
                    fieldWithPath("data.updatedAt").type(JsonFieldType.STRING).description("수정 일시")
                        .optional(),
                    fieldWithPath("data.deletedAt").type(JsonFieldType.STRING).description("삭제 일시")
                        .optional())));
    }

    @DisplayName("댓글 목록 조회 API 문서화")
    @Test
    void getComments() throws Exception {
        // given
        given(commentService.getComments(any(GetCommentsRequestDTO.class), any(Pageable.class)))
            .willReturn(CommentListResponseDTO.builder()
                .totalPages(1)
                .isLastPage(true)
                .totalComments(6)
                .comments(
                    List.of(
                        CommentResponseDTO.builder()
                            .commentId(1L)
                            .articleId(1L)
                            .memberId(1L)
                            .nickname("깜찍이")
                            .content("얼마나 걸리셨나요?")
                            .anonymity(false)
                            .parentCommentId(-1L)
                            .childCommentCount(2)
                            .createdAt("2024-01-01 12:00:00")
                            .updatedAt(null)
                            .deletedAt(null)
                            .build(),
                        CommentResponseDTO.builder()
                            .commentId(3L)
                            .articleId(1L)
                            .memberId(3L)
                            .nickname("멋쟁이")
                            .content("오...")
                            .anonymity(false)
                            .parentCommentId(-1L)
                            .childCommentCount(0)
                            .createdAt("2024-01-01 14:00:00")
                            .updatedAt(null)
                            .deletedAt(null)
                            .build(),
                        CommentResponseDTO.builder()
                            .commentId(4L)
                            .articleId(1L)
                            .memberId(3L)
                            .nickname("잼민이")
                            .content("굿")
                            .anonymity(false)
                            .parentCommentId(-1L)
                            .createdAt("2024-01-01 14:30:00")
                            .childCommentCount(1)
                            .updatedAt(null)
                            .deletedAt(null)
                            .build()))
                .build());

        // when, then
        mockMvc.perform(get("/api/v1/comments")
                .queryParam("articleId", "1")
                .queryParam("page", "0")
                .queryParam("pageSize", "10"))
            .andExpect(status().isOk())
            .andDo(document("comments-search",
                preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint()),
                requestParameters(
                    parameterWithName("articleId").description("게시글 식별자").optional(),
                    parameterWithName("memberId").description("회원 식별자").optional(),
                    parameterWithName("parentCommentId").description("대댓글을 조회할 댓글 식별자").optional(),
                    parameterWithName("pageSize").description("조회당 불러올 건 수").optional(),
                    parameterWithName("page").description("조회 페이지").optional()),
                responseFields(
                    fieldWithPath("code").type(JsonFieldType.NUMBER).description("응답 상태코드"),
                    fieldWithPath("message").type(JsonFieldType.STRING).description("메시지"),
                    fieldWithPath("data").type(JsonFieldType.OBJECT).description("응답데이터"),
                    fieldWithPath("data.totalPages").type(JsonFieldType.NUMBER)
                        .description("총 페이지 수"),
                    fieldWithPath("data.isLastPage").type(JsonFieldType.BOOLEAN)
                        .description("마지막 페이지 여부"),
                    fieldWithPath("data.totalComments").type(JsonFieldType.NUMBER)
                        .description("총 댓글 수"),
                    fieldWithPath("data.comments").type(JsonFieldType.ARRAY).description("댓글 목록"),
                    fieldWithPath("data.comments[].commentId").type(JsonFieldType.NUMBER)
                        .description("댓글 식별자"),
                    fieldWithPath("data.comments[].articleId").type(JsonFieldType.NUMBER)
                        .description("게시글 식별자"),
                    fieldWithPath("data.comments[].memberId").type(JsonFieldType.NUMBER)
                        .description("회원 식별자"),
                    fieldWithPath("data.comments[].nickname").type(JsonFieldType.STRING)
                        .description("작성자 닉네임"),
                    fieldWithPath("data.comments[].content").type(JsonFieldType.STRING)
                        .description("댓글 내용"),
                    fieldWithPath("data.comments[].anonymity").type(JsonFieldType.BOOLEAN)
                        .description("익명 여부"),
                    fieldWithPath("data.comments[].parentCommentId").type(JsonFieldType.NUMBER)
                        .description("부모 댓글 식별자"),
                    fieldWithPath("data.comments[].childCommentCount").type(JsonFieldType.NUMBER)
                        .description("대댓글 개수"),
                    fieldWithPath("data.comments[].createdAt").type(JsonFieldType.STRING)
                        .description("등록 일시"),
                    fieldWithPath("data.comments[].updatedAt").type(JsonFieldType.STRING)
                        .description("수정 일시").optional(),
                    fieldWithPath("data.comments[].deletedAt").type(JsonFieldType.STRING)
                        .description("삭제 일시").optional())));
    }

    @DisplayName("게시글 수정 API 문서화")
    @Test
    void postUpdate() throws Exception {
        // given
        String content = objectMapper.writeValueAsString(UpdateCommentRequestDTO.builder()
            .content("얼마나 걸리셨을까요?")
            .build());
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("MEMBER", 1L);
        given(commentService.updateComment(any(long.class), any(long.class),
            any(UpdateCommentRequestDTO.class)))
            .willReturn(CommentResponseDTO.builder()
                .commentId(1L)
                .articleId(1L)
                .memberId(1L)
                .nickname("깜찍이")
                .content("얼마나 걸리셨을까요?")
                .anonymity(false)
                .parentCommentId(-1L)
                .childCommentCount(2)
                .createdAt("2023-10-01 12:00:00")
                .updatedAt("2023-10-01 12:30:00")
                .deletedAt(null)
                .build());

        // when, then
        mockMvc.perform(patch("/api/v1/comments/{commentId}", 1L)
                .session(session)
                .content(content)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andDo(document("comment-update",
                preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint()),
                requestFields(
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
                    fieldWithPath("data.childCommentCount").type(JsonFieldType.NUMBER)
                        .description("대댓글 개수"),
                    fieldWithPath("data.createdAt").type(JsonFieldType.STRING).description("등록 일시"),
                    fieldWithPath("data.updatedAt").type(JsonFieldType.STRING).description("수정 일시"),
                    fieldWithPath("data.deletedAt").type(JsonFieldType.STRING).description("삭제 일시")
                        .optional())));
    }

    @DisplayName("게시글 삭제 API 문서화")
    @Test
    void postDelete() throws Exception {
        // given
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("MEMBER", 1L);
        given(commentService.deleteComment(any(long.class), any(long.class))).willReturn(
            CommentResponseDTO.builder()
                .commentId(1L)
                .articleId(1L)
                .memberId(1L)
                .nickname("깜찍이")
                .content("얼마나 걸리셨을까요?")
                .anonymity(false)
                .parentCommentId(-1L)
                .childCommentCount(2)
                .createdAt("2024-10-01 12:00:00")
                .updatedAt("2024-10-01 12:30:00")
                .deletedAt("2024-10-01 21:00:00")
                .build());

        // when, then
        mockMvc.perform(delete("/api/v1/comments/{commentId}", 1L)
                .session(session)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andDo(document("comment-delete",
                preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint()),
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
                    fieldWithPath("data.childCommentCount").type(JsonFieldType.NUMBER)
                        .description("대댓글 개수"),
                    fieldWithPath("data.createdAt").type(JsonFieldType.STRING).description("등록 일시"),
                    fieldWithPath("data.updatedAt").type(JsonFieldType.STRING).description("수정 일시"),
                    fieldWithPath("data.deletedAt").type(JsonFieldType.STRING)
                        .description("삭제 일시"))));
    }
}
