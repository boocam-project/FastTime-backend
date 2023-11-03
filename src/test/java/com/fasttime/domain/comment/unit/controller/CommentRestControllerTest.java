package com.fasttime.domain.comment.unit.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasttime.domain.comment.controller.CommentRestController;
import com.fasttime.domain.comment.dto.request.CreateCommentRequestDTO;
import com.fasttime.domain.comment.dto.request.GetCommentsRequestDTO;
import com.fasttime.domain.comment.dto.request.UpdateCommentRequestDTO;
import com.fasttime.domain.comment.dto.response.CommentResponseDTO;
import com.fasttime.domain.comment.service.CommentService;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(CommentRestController.class)
public class CommentRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    CommentService commentService;

    @Nested
    @DisplayName("createComment()은")
    class Context_createComment {

        @Test
        @DisplayName("댓글을 등록할 수 있다.")
        void _willSuccess() throws Exception {
            // given
            String content = new ObjectMapper().writeValueAsString(
                CreateCommentRequestDTO.builder()
                    .content("content")
                    .anonymity(false)
                    .parentCommentId(null)
                    .build());
            MockHttpSession session = new MockHttpSession();
            session.setAttribute("MEMBER", 1L);
            given(commentService.createComment(any(long.class), any(long.class),
                any(CreateCommentRequestDTO.class)))
                .willReturn(CommentResponseDTO.builder()
                    .commentId(1L)
                    .articleId(1L)
                    .memberId(1L)
                    .nickname("nickname")
                    .content("content")
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
                .andExpect(jsonPath("$.code").isNumber())
                .andExpect(jsonPath("$.message").isString())
                .andExpect(jsonPath("$.data").isMap())
                .andExpect(jsonPath("$.data.commentId").isNumber())
                .andExpect(jsonPath("$.data.articleId").isNumber())
                .andExpect(jsonPath("$.data.memberId").isNumber())
                .andExpect(jsonPath("$.data.nickname").isString())
                .andExpect(jsonPath("$.data.content").isString())
                .andExpect(jsonPath("$.data.anonymity").isBoolean())
                .andExpect(jsonPath("$.data.parentCommentId").isNumber())
                .andExpect(jsonPath("$.data.childCommentCount").isNumber())
                .andExpect(jsonPath("$.data.createdAt").isString())
                .andExpect(jsonPath("$.data.updatedAt").isEmpty())
                .andExpect(jsonPath("$.data.deletedAt").isEmpty())
                .andDo(print());
            verify(commentService, times(1)).createComment(any(long.class), any(long.class),
                any(CreateCommentRequestDTO.class));
        }

        @Nested
        @DisplayName("content가 ")
        class Element_content {

            @Test
            @DisplayName("null일 경우 댓글을 등록할 수 없다.")
            void null_willFail() throws Exception {
                // given
                String content = new ObjectMapper().writeValueAsString(
                    CreateCommentRequestDTO.builder()
                        .content(null)
                        .anonymity(false)
                        .parentCommentId(null)
                        .build());
                MockHttpSession session = new MockHttpSession();
                session.setAttribute("MEMBER", 1L);

                // when, then
                mockMvc.perform(post("/api/v1/comments/{articleId}", 1L)
                        .session(session)
                        .content(content)
                        .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.code").isNumber())
                    .andExpect(jsonPath("$.message").isString())
                    .andExpect(jsonPath("$.data").isEmpty())
                    .andDo(print());
                verify(commentService, never()).createComment(any(long.class), any(long.class),
                    any(CreateCommentRequestDTO.class));
            }

            @Test
            @DisplayName("빈 칸일 경우 댓글을 등록할 수 없다.")
            void blank_willFail() throws Exception {
                // given
                String content = new ObjectMapper().writeValueAsString(
                    CreateCommentRequestDTO.builder()
                        .content(" ")
                        .anonymity(false)
                        .parentCommentId(null)
                        .build());
                MockHttpSession session = new MockHttpSession();
                session.setAttribute("MEMBER", 1L);

                // when, then
                mockMvc.perform(post("/api/v1/comments/{articleId}", 1L)
                        .session(session)
                        .content(content)
                        .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.code").isNumber())
                    .andExpect(jsonPath("$.message").isString())
                    .andExpect(jsonPath("$.data").isEmpty())
                    .andDo(print());
                verify(commentService, never()).createComment(any(long.class), any(long.class),
                    any(CreateCommentRequestDTO.class));
            }
        }

        @Nested
        @DisplayName("anonymity가 ")
        class Element_anonymity {

            @Test
            @DisplayName("null일 경우 댓글을 등록할 수 없다.")
            void null_willFail() throws Exception {
                // given
                String content = new ObjectMapper().writeValueAsString(
                    CreateCommentRequestDTO.builder()
                        .content("test")
                        .anonymity(null)
                        .parentCommentId(null)
                        .build());
                MockHttpSession session = new MockHttpSession();
                session.setAttribute("MEMBER", 1L);

                // when, then
                mockMvc.perform(post("/api/v1/comments/{articleId}", 1L)
                        .session(session)
                        .content(content)
                        .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.code").isNumber())
                    .andExpect(jsonPath("$.message").isString())
                    .andExpect(jsonPath("$.data").isEmpty())
                    .andDo(print());
                verify(commentService, never()).createComment(any(long.class), any(long.class),
                    any(CreateCommentRequestDTO.class));
            }
        }
    }

    @Nested
    @DisplayName("getComments()는 ")
    class Context_getComments {

        @Test
        @DisplayName("해당 회원의 댓글을 조회할 수 있다.")
        void getCommentsByMemberId_willSuccess() throws Exception {
            // given
            given(commentService.getComments(any(GetCommentsRequestDTO.class))).willReturn(List.of(
                CommentResponseDTO.builder()
                    .commentId(1L)
                    .articleId(1L)
                    .memberId(1L)
                    .nickname("nickname")
                    .content("content1")
                    .anonymity(false)
                    .parentCommentId(-1L)
                    .childCommentCount(2)
                    .createdAt("2024-01-01 12:00:00")
                    .updatedAt(null)
                    .deletedAt(null)
                    .build(),
                CommentResponseDTO.builder()
                    .commentId(2L)
                    .articleId(2L)
                    .memberId(1L)
                    .nickname("nickname")
                    .content("content2")
                    .anonymity(false)
                    .parentCommentId(-1L)
                    .childCommentCount(1)
                    .createdAt("2024-01-01 13:00:00")
                    .updatedAt(null)
                    .deletedAt(null)
                    .build()));

            // when, then
            mockMvc.perform(get("/api/v1/comments")
                    .queryParam("memberId", "1")
                    .queryParam("page", "0")
                    .queryParam("pageSize", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").isNumber())
                .andExpect(jsonPath("$.message").isString())
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0]").isMap())
                .andExpect(jsonPath("$.data[0].commentId").isNumber())
                .andExpect(jsonPath("$.data[0].articleId").isNumber())
                .andExpect(jsonPath("$.data[0].memberId").isNumber())
                .andExpect(jsonPath("$.data[0].nickname").isString())
                .andExpect(jsonPath("$.data[0].content").isString())
                .andExpect(jsonPath("$.data[0].anonymity").isBoolean())
                .andExpect(jsonPath("$.data[0].parentCommentId").isNumber())
                .andExpect(jsonPath("$.data[0].childCommentCount").isNumber())
                .andExpect(jsonPath("$.data[0].createdAt").isString())
                .andExpect(jsonPath("$.data[0].updatedAt").isEmpty())
                .andExpect(jsonPath("$.data[0].deletedAt").isEmpty())
                .andDo(print());
            verify(commentService, times(1)).getComments(any(GetCommentsRequestDTO.class));
        }

        @Test
        @DisplayName("해당 게시물의 댓글을 조회할 수 있다.")
        void getCommentsByArticleId_willSuccess() throws Exception {
            // given
            given(commentService.getComments(any(GetCommentsRequestDTO.class))).willReturn(List.of(
                CommentResponseDTO.builder()
                    .commentId(1L)
                    .articleId(1L)
                    .memberId(1L)
                    .nickname("nickname1")
                    .content("content1")
                    .anonymity(false)
                    .parentCommentId(-1L)
                    .childCommentCount(1)
                    .createdAt("2024-01-01 12:00:00")
                    .updatedAt(null)
                    .deletedAt(null)
                    .build(),
                CommentResponseDTO.builder()
                    .commentId(3L)
                    .articleId(1L)
                    .memberId(3L)
                    .nickname("nickname3")
                    .content("content3")
                    .anonymity(false)
                    .parentCommentId(-1L)
                    .childCommentCount(0)
                    .createdAt("2024-01-01 13:00:00")
                    .updatedAt(null)
                    .deletedAt(null)
                    .build()));

            // when, then
            mockMvc.perform(get("/api/v1/comments")
                    .queryParam("articleId", "1")
                    .queryParam("page", "0")
                    .queryParam("pageSize", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").isNumber())
                .andExpect(jsonPath("$.message").isString())
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0]").isMap())
                .andExpect(jsonPath("$.data[0].commentId").isNumber())
                .andExpect(jsonPath("$.data[0].articleId").isNumber())
                .andExpect(jsonPath("$.data[0].memberId").isNumber())
                .andExpect(jsonPath("$.data[0].nickname").isString())
                .andExpect(jsonPath("$.data[0].content").isString())
                .andExpect(jsonPath("$.data[0].anonymity").isBoolean())
                .andExpect(jsonPath("$.data[0].parentCommentId").isNumber())
                .andExpect(jsonPath("$.data[0].childCommentCount").isNumber())
                .andExpect(jsonPath("$.data[0].createdAt").isString())
                .andExpect(jsonPath("$.data[0].updatedAt").isEmpty())
                .andExpect(jsonPath("$.data[0].deletedAt").isEmpty())
                .andDo(print());
            verify(commentService, times(1)).getComments(any(GetCommentsRequestDTO.class));
        }

        @Test
        @DisplayName("해당 댓글의 대댓글을 조회할 수 있다.")
        void getCommentsByParentCommentId_willSuccess() throws Exception {
            // given
            given(commentService.getComments(any(GetCommentsRequestDTO.class))).willReturn(List.of(
                CommentResponseDTO.builder()
                    .commentId(2L)
                    .articleId(1L)
                    .memberId(2L)
                    .nickname("nickname2")
                    .content("content2")
                    .anonymity(false)
                    .parentCommentId(1L)
                    .childCommentCount(0)
                    .createdAt("2024-01-01 12:30:00")
                    .updatedAt(null)
                    .deletedAt(null)
                    .build(),
                CommentResponseDTO.builder()
                    .commentId(4L)
                    .articleId(1L)
                    .memberId(1L)
                    .nickname("nickname1")
                    .content("content4")
                    .anonymity(false)
                    .parentCommentId(1L)
                    .childCommentCount(0)
                    .createdAt("2024-01-01 12:40:00")
                    .updatedAt(null)
                    .deletedAt(null)
                    .build(),
                CommentResponseDTO.builder()
                    .commentId(5L)
                    .articleId(1L)
                    .memberId(2L)
                    .nickname("nickname2")
                    .content("content5")
                    .anonymity(false)
                    .parentCommentId(1L)
                    .childCommentCount(0)
                    .createdAt("2024-01-01 12:50:00")
                    .updatedAt(null)
                    .deletedAt(null)
                    .build()));

            // when, then
            mockMvc.perform(get("/api/v1/comments")
                    .queryParam("parendCommentId", "1")
                    .queryParam("page", "0")
                    .queryParam("pageSize", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").isNumber())
                .andExpect(jsonPath("$.message").isString())
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0]").isMap())
                .andExpect(jsonPath("$.data[0].commentId").isNumber())
                .andExpect(jsonPath("$.data[0].articleId").isNumber())
                .andExpect(jsonPath("$.data[0].memberId").isNumber())
                .andExpect(jsonPath("$.data[0].nickname").isString())
                .andExpect(jsonPath("$.data[0].content").isString())
                .andExpect(jsonPath("$.data[0].anonymity").isBoolean())
                .andExpect(jsonPath("$.data[0].parentCommentId").isNumber())
                .andExpect(jsonPath("$.data[0].childCommentCount").isNumber())
                .andExpect(jsonPath("$.data[0].createdAt").isString())
                .andExpect(jsonPath("$.data[0].updatedAt").isEmpty())
                .andExpect(jsonPath("$.data[0].deletedAt").isEmpty())
                .andDo(print());
            verify(commentService, times(1)).getComments(any(GetCommentsRequestDTO.class));
        }
    }

    @Nested
    @DisplayName("updateComment()는 ")
    class Context_updateComment {

        @Test
        @DisplayName("댓글을 수정할 수 있다.")
        void _willSuccess() throws Exception {
            // given
            String content = new ObjectMapper().writeValueAsString(
                UpdateCommentRequestDTO.builder().content("content2").build());
            given(commentService.updateComment(any(long.class),
                any(UpdateCommentRequestDTO.class))).willReturn(
                CommentResponseDTO.builder()
                    .commentId(1L)
                    .articleId(1L)
                    .memberId(1L)
                    .nickname("nickname")
                    .content("content1")
                    .anonymity(false)
                    .parentCommentId(-1L)
                    .createdAt("2024-01-01 12:00:00")
                    .updatedAt("2024-01-01 12:30:00")
                    .deletedAt(null)
                    .childCommentCount(0)
                    .build());

            // when, then
            mockMvc.perform(patch("/api/v1/comments/{commentId}", 1L)
                    .content(content)
                    .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").isNumber())
                .andExpect(jsonPath("$.message").isString())
                .andExpect(jsonPath("$.data").isMap())
                .andExpect(jsonPath("$.data.commentId").isNumber())
                .andExpect(jsonPath("$.data.articleId").isNumber())
                .andExpect(jsonPath("$.data.memberId").isNumber())
                .andExpect(jsonPath("$.data.nickname").isString())
                .andExpect(jsonPath("$.data.content").isString())
                .andExpect(jsonPath("$.data.anonymity").isBoolean())
                .andExpect(jsonPath("$.data.parentCommentId").isNumber())
                .andExpect(jsonPath("$.data.childCommentCount").isNumber())
                .andExpect(jsonPath("$.data.createdAt").isString())
                .andExpect(jsonPath("$.data.updatedAt").isString())
                .andExpect(jsonPath("$.data.deletedAt").isEmpty())
                .andDo(print());
            verify(commentService, times(1)).updateComment(any(long.class),
                any(UpdateCommentRequestDTO.class));
        }

        @Nested
        @DisplayName("content가 ")
        class Element_content {

            @Test
            @DisplayName("null일 경우 댓글을 수정할 수 없다.")
            void null_willFail() throws Exception {
                // given
                String content = new ObjectMapper().writeValueAsString(
                    UpdateCommentRequestDTO.builder()
                        .content(null)
                        .build());
                // when, then
                mockMvc.perform(patch("/api/v1/comments/{commentId}", 1L)
                        .content(content)
                        .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.code").isNumber())
                    .andExpect(jsonPath("$.message").isString())
                    .andExpect(jsonPath("$.data").isEmpty())
                    .andDo(print());
                verify(commentService, never()).updateComment(any(long.class),
                    any(UpdateCommentRequestDTO.class));
            }

            @Test
            @DisplayName("빈 칸일 경우 댓글을 수정할 수 없다.")
            void blank_willFail() throws Exception {
                // given
                String content = new ObjectMapper().writeValueAsString(
                    UpdateCommentRequestDTO.builder()
                        .content(" ")
                        .build());
                // when, then
                mockMvc.perform(patch("/api/v1/comments/{commentId}", 1L)
                        .content(content)
                        .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.code").isNumber())
                    .andExpect(jsonPath("$.message").isString())
                    .andExpect(jsonPath("$.data").isEmpty())
                    .andDo(print());
                verify(commentService, never()).updateComment(any(long.class),
                    any(UpdateCommentRequestDTO.class));
            }
        }
    }

    @Nested
    @DisplayName("deleteComment()은 ")
    class Context_deleteComment {

        @Test
        @DisplayName("댓글을 삭제할 수 있다.")
        void _willSuccess() throws Exception {
            // given
            given(commentService.deleteComment(any(long.class))).willReturn(
                CommentResponseDTO.builder()
                    .commentId(1L)
                    .articleId(1L)
                    .memberId(1L)
                    .nickname("nickname1")
                    .content("modified")
                    .anonymity(false)
                    .parentCommentId(-1L)
                    .createdAt("2024-01-01 12:00:00")
                    .updatedAt("2024-01-01 13:00:00")
                    .deletedAt("2024-01-01 18:00:00")
                    .childCommentCount(0)
                    .build());

            // when, then
            mockMvc.perform(delete("/api/v1/comments/{commentId}", 1L)
                    .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").isNumber())
                .andExpect(jsonPath("$.message").isString())
                .andExpect(jsonPath("$.data").isMap())
                .andExpect(jsonPath("$.data.commentId").isNumber())
                .andExpect(jsonPath("$.data.articleId").isNumber())
                .andExpect(jsonPath("$.data.memberId").isNumber())
                .andExpect(jsonPath("$.data.nickname").isString())
                .andExpect(jsonPath("$.data.content").isString())
                .andExpect(jsonPath("$.data.anonymity").isBoolean())
                .andExpect(jsonPath("$.data.parentCommentId").isNumber())
                .andExpect(jsonPath("$.data.childCommentCount").isNumber())
                .andExpect(jsonPath("$.data.createdAt").isString())
                .andExpect(jsonPath("$.data.updatedAt").isString())
                .andExpect(jsonPath("$.data.deletedAt").isString())
                .andDo(print());
            verify(commentService, times(1)).deleteComment(any(long.class));
        }
    }
}
