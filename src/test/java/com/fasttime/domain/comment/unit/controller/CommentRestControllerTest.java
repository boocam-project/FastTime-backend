package com.fasttime.domain.comment.unit.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
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
import com.fasttime.domain.comment.dto.request.UpdateCommentRequestDTO;
import com.fasttime.domain.comment.dto.response.MyPageCommentResponseDTO;
import com.fasttime.domain.comment.dto.response.PostCommentResponseDTO;
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
            doNothing().when(commentService)
                .createComment(any(CreateCommentRequestDTO.class), any(Long.class));
            String json = new ObjectMapper().writeValueAsString(request);
            MockHttpSession session = new MockHttpSession();
            session.setAttribute("MEMBER", 1L);

            // when, then
            mockMvc.perform(
                    post("/api/v1/comment").content(json).contentType(MediaType.APPLICATION_JSON)
                        .session(session)).andExpect(status().isCreated())
                .andExpect(jsonPath("$.code").exists()).andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.data").isEmpty()).andDo(print());
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
                doNothing().when(commentService)
                    .createComment(any(CreateCommentRequestDTO.class), any(Long.class));
                String json = new ObjectMapper().writeValueAsString(request);
                MockHttpSession session = new MockHttpSession();
                session.setAttribute("MEMBER", 1L);

                // when, then
                mockMvc.perform(
                        post("/api/v1/comment").content(json).contentType(MediaType.APPLICATION_JSON)
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
                doNothing().when(commentService)
                    .createComment(any(CreateCommentRequestDTO.class), any(Long.class));
                String json = new ObjectMapper().writeValueAsString(request);
                MockHttpSession session = new MockHttpSession();
                session.setAttribute("MEMBER", 1L);

                // when, then
                mockMvc.perform(
                        post("/api/v1/comment").content(json).contentType(MediaType.APPLICATION_JSON)
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
                doNothing().when(commentService)
                    .createComment(any(CreateCommentRequestDTO.class), any(Long.class));
                String json = new ObjectMapper().writeValueAsString(request);
                MockHttpSession session = new MockHttpSession();
                session.setAttribute("MEMBER", 1L);

                // when, then
                mockMvc.perform(
                        post("/api/v1/comment").content(json).contentType(MediaType.APPLICATION_JSON)
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
                doNothing().when(commentService)
                    .createComment(any(CreateCommentRequestDTO.class), any(Long.class));
                String json = new ObjectMapper().writeValueAsString(request);
                MockHttpSession session = new MockHttpSession();
                session.setAttribute("MEMBER", 1L);

                // when, then
                mockMvc.perform(
                        post("/api/v1/comment").content(json).contentType(MediaType.APPLICATION_JSON)
                            .session(session)).andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.code").exists())
                    .andExpect(jsonPath("$.message").exists())
                    .andExpect(jsonPath("$.data").isEmpty()).andDo(print());
            }
        }
    }

    @Nested
    @DisplayName("getCommentsByMemberId()은 ")
    class Context_getCommentsByMemberId {

        @Test
        @DisplayName("해당 회원의 댓글을 조회할 수 있다.")
        void _willSuccess() throws Exception {
            // given
            List<MyPageCommentResponseDTO> comments = new ArrayList<>();
            comments.add(
                MyPageCommentResponseDTO.builder().id(0L).postId(0L).nickname("testNickname")
                    .content("test").anonymity(false).parentCommentId(null).build());
            given(commentService.getCommentsByMemberId(any(long.class))).willReturn(comments);
            MockHttpSession session = new MockHttpSession();
            session.setAttribute("MEMBER", 1L);

            // when, then
            mockMvc.perform(get("/api/v1/comment/my-page").session(session))
                .andExpect(status().isOk()).andExpect(jsonPath("$.code").exists())
                .andExpect(jsonPath("$.message").exists()).andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].id").exists())
                .andExpect(jsonPath("$.data[0].postId").exists())
                .andExpect(jsonPath("$.data[0].nickname").exists())
                .andExpect(jsonPath("$.data[0].content").exists())
                .andExpect(jsonPath("$.data[0].anonymity").exists())
                .andExpect(jsonPath("$.data[0].parentCommentId").isEmpty()).andDo(print());

            verify(commentService, times(1)).getCommentsByMemberId(any(long.class));
        }
    }

    @Nested
    @DisplayName("getCommentsByPostId()은 ")
    class Context_getCommentByPostId {

        @Test
        @DisplayName("해당 게시물의 댓글을 조회할 수 있다.")
        void _willSuccess() throws Exception {
            // given
            List<PostCommentResponseDTO> comments = new ArrayList<>();
            comments.add(
                PostCommentResponseDTO.builder().id(0L).memberId(0L).nickname("testNickname")
                    .content("test").anonymity(false).parentCommentId(null).build());
            given(commentService.getCommentsByPostId(any(long.class))).willReturn(comments);

            // when, then
            mockMvc.perform(get("/api/v1/comment/0")).andExpect(status().isOk())
                .andExpect(jsonPath("$.code").exists()).andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].id").exists())
                .andExpect(jsonPath("$.data[0].memberId").exists())
                .andExpect(jsonPath("$.data[0].nickname").exists())
                .andExpect(jsonPath("$.data[0].content").exists())
                .andExpect(jsonPath("$.data[0].anonymity").exists())
                .andExpect(jsonPath("$.data[0].parentCommentId").isEmpty()).andDo(print());

            verify(commentService, times(1)).getCommentsByPostId(any(long.class));
        }
    }

    @Nested
    @DisplayName("updateComment()는 ")
    class Context_updateComment {

        @Test
        @DisplayName("댓글을 수정할 수 있다.")
        void _willSuccess() throws Exception {
            // given
            UpdateCommentRequestDTO request = UpdateCommentRequestDTO.builder().id(0L)
                .content("modified").build();
            doNothing().when(commentService).updateComment(any(UpdateCommentRequestDTO.class));
            String json = new ObjectMapper().writeValueAsString(request);

            // when, then
            mockMvc.perform(
                    patch("/api/v1/comment").content(json).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andExpect(jsonPath("$.code").exists())
                .andExpect(jsonPath("$.message").exists()).andExpect(jsonPath("$.data").isEmpty())
                .andDo(print());
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
                doNothing().when(commentService).updateComment(any(UpdateCommentRequestDTO.class));
                String json = new ObjectMapper().writeValueAsString(request);

                // when, then
                mockMvc.perform(
                        patch("/api/v1/comment").content(json).contentType(MediaType.APPLICATION_JSON))
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
                doNothing().when(commentService).updateComment(any(UpdateCommentRequestDTO.class));
                String json = new ObjectMapper().writeValueAsString(request);

                // when, then
                mockMvc.perform(
                        patch("/api/v1/comment").content(json).contentType(MediaType.APPLICATION_JSON))
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
                doNothing().when(commentService).updateComment(any(UpdateCommentRequestDTO.class));
                String json = new ObjectMapper().writeValueAsString(request);

                // when, then
                mockMvc.perform(
                        patch("/api/v1/comment").content(json).contentType(MediaType.APPLICATION_JSON))
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
            doNothing().when(commentService).deleteComment(any(DeleteCommentRequestDTO.class));

            // when, then
            mockMvc.perform(
                    delete("/api/v1/comment").content(json).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andExpect(jsonPath("$.code").exists())
                .andExpect(jsonPath("$.message").exists()).andExpect(jsonPath("$.data").isEmpty())
                .andDo(print());
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
                mockMvc.perform(
                        delete("/api/v1/comment").content(json).contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest()).andExpect(jsonPath("$.code").exists())
                    .andExpect(jsonPath("$.message").exists())
                    .andExpect(jsonPath("$.data").isEmpty()).andDo(print());
                verify(commentService, never()).deleteComment(any(DeleteCommentRequestDTO.class));
            }
        }
    }
}
