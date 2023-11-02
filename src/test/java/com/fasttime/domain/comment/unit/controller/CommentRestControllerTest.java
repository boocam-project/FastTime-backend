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
import com.fasttime.domain.comment.dto.request.DeleteCommentRequestDTO;
import com.fasttime.domain.comment.dto.request.GetCommentsRequestDTO;
import com.fasttime.domain.comment.dto.request.UpdateCommentRequestDTO;
import com.fasttime.domain.comment.dto.response.CommentResponseDTO;
import com.fasttime.domain.comment.service.CommentService;
import java.util.ArrayList;
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
            CreateCommentRequestDTO request = CreateCommentRequestDTO.builder().postId(1L)
                .content("test").anonymity(false).parentCommentId(null).build();
            given(commentService
                .createComment(any(CreateCommentRequestDTO.class), any(Long.class))).willReturn(CommentResponseDTO.builder().commentId(1L).articleId(1L).memberId(1L).nickname("nickname1")
                .content("test").anonymity(false).parentCommentId(-1L)
                .createdAt("2023-10-01 12:01:23").updatedAt(null).deletedAt(null)
                .childCommentCount(0).build());
            String json = new ObjectMapper().writeValueAsString(request);
            MockHttpSession session = new MockHttpSession();
            session.setAttribute("MEMBER", 1L);

            // when, then
            mockMvc.perform(
                    post("/api/v1/comments").content(json).contentType(MediaType.APPLICATION_JSON)
                        .session(session)).andExpect(status().isCreated())
                .andExpect(jsonPath("$.code").exists()).andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.data").isMap())
                .andExpect(jsonPath("$.data.commentId").exists())
                .andExpect(jsonPath("$.data.articleId").exists())
                .andExpect(jsonPath("$.data.memberId").exists())
                .andExpect(jsonPath("$.data.nickname").exists())
                .andExpect(jsonPath("$.data.content").exists())
                .andExpect(jsonPath("$.data.anonymity").exists())
                .andExpect(jsonPath("$.data.parentCommentId").exists())
                .andExpect(jsonPath("$.data.childCommentCount").exists()).andDo(print());
        }

        @Nested
        @DisplayName("postId가 ")
        class Element_postId {

            @Test
            @DisplayName("null일 경우 댓글을 등록할 수 없다.")
            void null_willFail() throws Exception {
                // given
                CreateCommentRequestDTO request = CreateCommentRequestDTO.builder().postId(null)
                    .content("test").anonymity(false).parentCommentId(null).build();
                String json = new ObjectMapper().writeValueAsString(request);
                MockHttpSession session = new MockHttpSession();
                session.setAttribute("MEMBER", 1L);

                // when, then
                mockMvc.perform(
                        post("/api/v1/comments").content(json).contentType(MediaType.APPLICATION_JSON)
                            .session(session)).andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.code").exists())
                    .andExpect(jsonPath("$.message").exists())
                    .andExpect(jsonPath("$.data").isEmpty()).andDo(print());
            }
        }

        @Nested
        @DisplayName("content가 ")
        class Element_content {

            @Test
            @DisplayName("null일 경우 댓글을 등록할 수 없다.")
            void null_willFail() throws Exception {
                // given
                CreateCommentRequestDTO request = CreateCommentRequestDTO.builder().postId(0L)
                    .content(null).anonymity(false).parentCommentId(null).build();
                String json = new ObjectMapper().writeValueAsString(request);
                MockHttpSession session = new MockHttpSession();
                session.setAttribute("MEMBER", 1L);

                // when, then
                mockMvc.perform(
                        post("/api/v1/comments").content(json).contentType(MediaType.APPLICATION_JSON)
                            .session(session)).andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.code").exists())
                    .andExpect(jsonPath("$.message").exists())
                    .andExpect(jsonPath("$.data").isEmpty()).andDo(print());
            }

            @Test
            @DisplayName("빈 칸일 경우 댓글을 등록할 수 없다.")
            void blank_willFail() throws Exception {
                // given
                CreateCommentRequestDTO request = CreateCommentRequestDTO.builder().postId(0L)
                    .content(" ").anonymity(false).parentCommentId(null).build();
                String json = new ObjectMapper().writeValueAsString(request);
                MockHttpSession session = new MockHttpSession();
                session.setAttribute("MEMBER", 1L);

                // when, then
                mockMvc.perform(
                        post("/api/v1/comments").content(json).contentType(MediaType.APPLICATION_JSON)
                            .session(session)).andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.code").exists())
                    .andExpect(jsonPath("$.message").exists())
                    .andExpect(jsonPath("$.data").isEmpty()).andDo(print());
            }
        }

        @Nested
        @DisplayName("anonymity가 ")
        class Element_anonymity {

            @Test
            @DisplayName("null일 경우 댓글을 등록할 수 없다.")
            void null_willFail() throws Exception {
                // given
                CreateCommentRequestDTO request = CreateCommentRequestDTO.builder().postId(0L)
                    .content("test").anonymity(null).parentCommentId(null).build();
                String json = new ObjectMapper().writeValueAsString(request);
                MockHttpSession session = new MockHttpSession();
                session.setAttribute("MEMBER", 1L);

                // when, then
                mockMvc.perform(
                        post("/api/v1/comments").content(json).contentType(MediaType.APPLICATION_JSON)
                            .session(session)).andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.code").exists())
                    .andExpect(jsonPath("$.message").exists())
                    .andExpect(jsonPath("$.data").isEmpty()).andDo(print());
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
            List<CommentResponseDTO> comments = new ArrayList<>();
            comments.add(CommentResponseDTO.builder().commentId(1L).articleId(1L).memberId(1L)
                .nickname("testNickname").content("test1").anonymity(false).parentCommentId(-1L)
                .childCommentCount(0).build());
            comments.add(CommentResponseDTO.builder().commentId(2L).articleId(2L).memberId(1L)
                .nickname("testNickname").content("test2").anonymity(false).parentCommentId(-1L)
                .childCommentCount(2).build());
            given(commentService.getComments(any(GetCommentsRequestDTO.class))).willReturn(comments);

            // when, then
            mockMvc.perform(
                    get("/api/v1/comments").queryParam("memberId", "1").queryParam("page", "0")
                        .queryParam("pageSize", "10")).andExpect(status().isOk())
                .andExpect(jsonPath("$.code").exists()).andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].commentId").exists())
                .andExpect(jsonPath("$.data[0].articleId").exists())
                .andExpect(jsonPath("$.data[0].nickname").exists())
                .andExpect(jsonPath("$.data[0].content").exists())
                .andExpect(jsonPath("$.data[0].anonymity").exists())
                .andExpect(jsonPath("$.data[0].parentCommentId").exists())
                .andExpect(jsonPath("$.data[0].childCommentCount").exists()).andDo(print());

            verify(commentService, times(1)).getComments(any(GetCommentsRequestDTO.class));
        }

        @Test
        @DisplayName("해당 게시물의 댓글을 조회할 수 있다.")
        void getCommentsByArticleId_willSuccess() throws Exception {
            // given
            List<CommentResponseDTO> comments = new ArrayList<>();
            comments.add(CommentResponseDTO.builder().commentId(1L).articleId(1L).memberId(1L)
                .nickname("testNickname").content("test1").anonymity(false).parentCommentId(-1L)
                .childCommentCount(1).build());
            comments.add(CommentResponseDTO.builder().commentId(3L).articleId(1L).memberId(3L)
                .nickname("testNickname").content("test3").anonymity(false).parentCommentId(-1L)
                .childCommentCount(0).build());
            given(commentService.getComments(any(GetCommentsRequestDTO.class))).willReturn(comments);

            // when, then
            mockMvc.perform(
                    get("/api/v1/comments").queryParam("articleId", "1").queryParam("page", "0")
                        .queryParam("pageSize", "10")).andExpect(status().isOk())
                .andExpect(jsonPath("$.code").exists()).andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].commentId").exists())
                .andExpect(jsonPath("$.data[0].articleId").exists())
                .andExpect(jsonPath("$.data[0].memberId").exists())
                .andExpect(jsonPath("$.data[0].nickname").exists())
                .andExpect(jsonPath("$.data[0].content").exists())
                .andExpect(jsonPath("$.data[0].anonymity").exists())
                .andExpect(jsonPath("$.data[0].parentCommentId").exists())
                .andExpect(jsonPath("$.data[0].childCommentCount").exists()).andDo(print());

            verify(commentService, times(1)).getComments(any(GetCommentsRequestDTO.class));
        }

        @Test
        @DisplayName("해당 댓글의 대댓글을 조회할 수 있다.")
        void getCommentsByParentCommentId_willSuccess() throws Exception {
            // given
            List<CommentResponseDTO> comments = new ArrayList<>();
            comments.add(CommentResponseDTO.builder().commentId(2L).articleId(1L).memberId(2L)
                .nickname("testNickname").content("test2").anonymity(false).parentCommentId(1L)
                .childCommentCount(0).build());
            comments.add(CommentResponseDTO.builder().commentId(4L).articleId(1L).memberId(1L)
                .nickname("testNickname").content("test4").anonymity(false).parentCommentId(1L)
                .childCommentCount(0).build());
            comments.add(CommentResponseDTO.builder().commentId(5L).articleId(1L).memberId(2L)
                .nickname("testNickname").content("test5").anonymity(false).parentCommentId(1L)
                .childCommentCount(0).build());
            given(commentService.getComments(any(GetCommentsRequestDTO.class))).willReturn(comments);

            // when, then
            mockMvc.perform(
                    get("/api/v1/comments").queryParam("parendCommentId", "1").queryParam("page", "0")
                        .queryParam("pageSize", "10")).andExpect(status().isOk())
                .andExpect(jsonPath("$.code").exists()).andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].commentId").exists())
                .andExpect(jsonPath("$.data[0].articleId").exists())
                .andExpect(jsonPath("$.data[0].memberId").exists())
                .andExpect(jsonPath("$.data[0].nickname").exists())
                .andExpect(jsonPath("$.data[0].content").exists())
                .andExpect(jsonPath("$.data[0].anonymity").exists())
                .andExpect(jsonPath("$.data[0].parentCommentId").exists())
                .andExpect(jsonPath("$.data[0].childCommentCount").exists()).andDo(print());

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
            UpdateCommentRequestDTO request = UpdateCommentRequestDTO.builder().id(1L)
                .content("modified").build();
            given(commentService
                .updateComment(any(UpdateCommentRequestDTO.class))).willReturn(CommentResponseDTO.builder().commentId(1L).articleId(1L).memberId(1L).nickname("nickname1")
                .content("modified").anonymity(false).parentCommentId(-1L)
                .createdAt("2023-10-01 12:01:23").updatedAt("2023-10-01 13:00:07").deletedAt(null)
                .childCommentCount(0).build());
            String json = new ObjectMapper().writeValueAsString(request);

            // when, then
            mockMvc.perform(
                    patch("/api/v1/comments").content(json).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andExpect(jsonPath("$.code").exists())
                .andExpect(jsonPath("$.message").exists()).andExpect(jsonPath("$.data").isMap())
                .andExpect(jsonPath("$.data.commentId").exists())
                .andExpect(jsonPath("$.data.articleId").exists())
                .andExpect(jsonPath("$.data.memberId").exists())
                .andExpect(jsonPath("$.data.nickname").exists())
                .andExpect(jsonPath("$.data.content").exists())
                .andExpect(jsonPath("$.data.anonymity").exists())
                .andExpect(jsonPath("$.data.parentCommentId").exists())
                .andExpect(jsonPath("$.data.childCommentCount").exists()).andDo(print());
        }

        @Nested
        @DisplayName("id가 ")
        class Element_id {

            @Test
            @DisplayName("null일 경우 댓글을 수정할 수 없다.")
            void null_willFail() throws Exception {
                // given
                UpdateCommentRequestDTO request = UpdateCommentRequestDTO.builder().id(null)
                    .content("modified").build();
                String json = new ObjectMapper().writeValueAsString(request);

                // when, then
                mockMvc.perform(
                        patch("/api/v1/comments").content(json).contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest()).andExpect(jsonPath("$.code").exists())
                    .andExpect(jsonPath("$.message").exists())
                    .andExpect(jsonPath("$.data").isEmpty()).andDo(print());
            }
        }

        @Nested
        @DisplayName("content가 ")
        class Element_content {

            @Test
            @DisplayName("null일 경우 댓글을 수정할 수 없다.")
            void null_willFail() throws Exception {
                // given
                UpdateCommentRequestDTO request = UpdateCommentRequestDTO.builder().id(0L)
                    .content(null).build();
                String json = new ObjectMapper().writeValueAsString(request);

                // when, then
                mockMvc.perform(
                        patch("/api/v1/comments").content(json).contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest()).andExpect(jsonPath("$.code").exists())
                    .andExpect(jsonPath("$.message").exists())
                    .andExpect(jsonPath("$.data").isEmpty()).andDo(print());
            }

            @Test
            @DisplayName("빈 칸일 경우 댓글을 수정할 수 없다.")
            void blank_willFail() throws Exception {
                // given
                UpdateCommentRequestDTO request = UpdateCommentRequestDTO.builder().id(0L)
                    .content(" ").build();
                String json = new ObjectMapper().writeValueAsString(request);

                // when, then
                mockMvc.perform(
                        patch("/api/v1/comments").content(json).contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest()).andExpect(jsonPath("$.code").exists())
                    .andExpect(jsonPath("$.message").exists())
                    .andExpect(jsonPath("$.data").isEmpty()).andDo(print());
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
            DeleteCommentRequestDTO request = DeleteCommentRequestDTO.builder().id(0L).build();
            String json = new ObjectMapper().writeValueAsString(request);
            given(commentService
                .deleteComment(any(DeleteCommentRequestDTO.class))).willReturn(CommentResponseDTO.builder().commentId(1L).articleId(1L).memberId(1L).nickname("nickname1")
                .content("modified").anonymity(false).parentCommentId(-1L)
                .createdAt("2023-10-01 12:01:23").updatedAt("2023-10-03 21:17:03").deletedAt("2023-10-03 21:17:03")
                .childCommentCount(0).build());

            // when, then
            mockMvc.perform(
                    delete("/api/v1/comments").content(json).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andExpect(jsonPath("$.code").exists())
                .andExpect(jsonPath("$.message").exists()).andExpect(jsonPath("$.data").isMap())
                .andExpect(jsonPath("$.data.commentId").exists())
                .andExpect(jsonPath("$.data.articleId").exists())
                .andExpect(jsonPath("$.data.memberId").exists())
                .andExpect(jsonPath("$.data.nickname").exists())
                .andExpect(jsonPath("$.data.content").exists())
                .andExpect(jsonPath("$.data.anonymity").exists())
                .andExpect(jsonPath("$.data.parentCommentId").exists())
                .andExpect(jsonPath("$.data.childCommentCount").exists()).andDo(print());
        }

        @Nested
        @DisplayName("id가 ")
        class Element_id {

            @Test
            @DisplayName("null일 경우 댓글을 삭제할 수 없다.")
            void null_willFail() throws Exception {
                // given
                DeleteCommentRequestDTO request = DeleteCommentRequestDTO.builder().id(null)
                    .build();
                String json = new ObjectMapper().writeValueAsString(request);

                // when, then
                mockMvc.perform(delete("/api/v1/comments").content(json)
                        .contentType(MediaType.APPLICATION_JSON)).andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.code").exists())
                    .andExpect(jsonPath("$.message").exists())
                    .andExpect(jsonPath("$.data").isEmpty()).andDo(print());
                verify(commentService, never()).deleteComment(any(DeleteCommentRequestDTO.class));
            }
        }
    }
}
