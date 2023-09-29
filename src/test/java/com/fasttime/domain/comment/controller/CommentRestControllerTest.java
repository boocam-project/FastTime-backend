package com.fasttime.domain.comment.controller;

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
import com.fasttime.domain.comment.dto.CommentDTO;
import com.fasttime.domain.comment.dto.request.CreateCommentRequest;
import com.fasttime.domain.comment.dto.request.DeleteCommentRequest;
import com.fasttime.domain.comment.dto.request.UpdateCommentRequest;
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
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(CommentRestController.class)
public class CommentRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    CommentService commentService;

    @Nested
    @DisplayName("create()은")
    class Context_create {

        @Test
        @DisplayName("댓글을 등록할 수 있다.")
        void _willSuccess() throws Exception {
            // given
            CreateCommentRequest request = CreateCommentRequest.builder().postId(0L).memberId(0L)
                .content("test").anonymity(false).parentCommentId(null).build();
            CommentDTO commentDto = CommentDTO.builder().id(0L).postId(0L).memberId(0L)
                .content("test").anonymity(false).parentCommentId(null).build();
            given(commentService.createComment(any(CreateCommentRequest.class))).willReturn(
                commentDto);
            String json = new ObjectMapper().writeValueAsString(request);

            // when, then
            mockMvc.perform(
                    post("/api/v1/comment").content(json).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated()).andExpect(jsonPath("$.data.id").exists())
                .andExpect(jsonPath("$.data.postId").exists())
                .andExpect(jsonPath("$.data.memberId").exists())
                .andExpect(jsonPath("$.data.content").exists())
                .andExpect(jsonPath("$.data.anonymity").exists())
                .andExpect(jsonPath("$.data.parentCommentId").isEmpty()).andDo(print());

            verify(commentService, times(1)).createComment(any(CreateCommentRequest.class));
        }

        @Nested
        @DisplayName("postId가 ")
        class Element_postId {

            @Test
            @DisplayName("null일 경우 댓글을 등록할 수 없다.")
            void null_willFail() throws Exception {
                // given
                CreateCommentRequest request = CreateCommentRequest.builder().postId(null)
                    .memberId(0L).content("test").anonymity(false).parentCommentId(null).build();
                CommentDTO commentDto = CommentDTO.builder().id(0L).postId(0L).memberId(0L)
                    .content("test").anonymity(false).parentCommentId(null).build();
                given(commentService.createComment(any(CreateCommentRequest.class))).willReturn(
                    commentDto);
                String json = new ObjectMapper().writeValueAsString(request);

                // when, then
                mockMvc.perform(
                        post("/api/v1/comment").content(json).contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest()).andExpect(jsonPath("$.message").exists())
                    .andDo(print());

                verify(commentService, never()).createComment(any(CreateCommentRequest.class));
            }
        }

        @Nested
        @DisplayName("memberId가 ")
        class Element_memberId {

            @Test
            @DisplayName("null일 경우 댓글을 등록할 수 없다.")
            void null_willFail() throws Exception {
                // given
                CreateCommentRequest request = CreateCommentRequest.builder().postId(0L)
                    .memberId(null).content("test").anonymity(false).parentCommentId(null).build();
                CommentDTO commentDto = CommentDTO.builder().id(0L).postId(0L).memberId(0L)
                    .content("test").anonymity(false).parentCommentId(null).build();
                given(commentService.createComment(any(CreateCommentRequest.class))).willReturn(
                    commentDto);
                String json = new ObjectMapper().writeValueAsString(request);

                // when, then
                mockMvc.perform(
                        post("/api/v1/comment").content(json).contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest()).andExpect(jsonPath("$.message").exists())
                    .andDo(print());

                verify(commentService, never()).createComment(any(CreateCommentRequest.class));
            }
        }

        @Nested
        @DisplayName("content가 ")
        class Element_content {

            @Test
            @DisplayName("null일 경우 댓글을 등록할 수 없다.")
            void null_willFail() throws Exception {
                // given
                CreateCommentRequest request = CreateCommentRequest.builder().postId(0L)
                    .memberId(0L).content(null).anonymity(false).parentCommentId(null).build();
                CommentDTO commentDto = CommentDTO.builder().id(0L).postId(0L).memberId(0L)
                    .content("test").anonymity(false).parentCommentId(null).build();
                given(commentService.createComment(any(CreateCommentRequest.class))).willReturn(
                    commentDto);
                String json = new ObjectMapper().writeValueAsString(request);

                // whCreate
                mockMvc.perform(
                        post("/api/v1/comment").content(json).contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest()).andExpect(jsonPath("$.message").exists())
                    .andDo(print());

                verify(commentService, never()).createComment(any(CreateCommentRequest.class));
            }

            @Test
            @DisplayName("빈 칸일 경우 댓글을 등록할 수 없다.")
            void blank_willFail() throws Exception {
                // given
                CreateCommentRequest request = CreateCommentRequest.builder().postId(0L)
                    .memberId(0L).content(" ").anonymity(false).parentCommentId(null).build();
                CommentDTO commentDto = CommentDTO.builder().id(0L).postId(0L).memberId(0L)
                    .content("test").anonymity(false).parentCommentId(null).build();
                given(commentService.createComment(any(CreateCommentRequest.class))).willReturn(
                    commentDto);
                String json = new ObjectMapper().writeValueAsString(request);

                // when, then
                mockMvc.perform(
                        post("/api/v1/comment").content(json).contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest()).andExpect(jsonPath("$.message").exists())
                    .andDo(print());

                verify(commentService, never()).createComment(any(CreateCommentRequest.class));
            }
        }

        @Nested
        @DisplayName("anonymity가 ")
        class Element_anonymity {

            @Test
            @DisplayName("null일 경우 댓글을 등록할 수 없다.")
            void null_willFail() throws Exception {
                // given
                CreateCommentRequest request = CreateCommentRequest.builder().postId(0L)
                    .memberId(0L).content("test").anonymity(null).parentCommentId(null).build();
                CommentDTO commentDto = CommentDTO.builder().id(0L).postId(0L).memberId(0L)
                    .content("test").anonymity(false).parentCommentId(null).build();
                given(commentService.createComment(any(CreateCommentRequest.class))).willReturn(
                    commentDto);
                String json = new ObjectMapper().writeValueAsString(request);

                // when, then
                mockMvc.perform(
                        post("/api/v1/comment").content(json).contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest()).andExpect(jsonPath("$.message").exists())
                    .andDo(print());

                verify(commentService, never()).createComment(any(CreateCommentRequest.class));
            }
        }
    }

    @Nested
    @DisplayName("getCommentByMemberId()은 ")
    class Context_getCommentByMemberId {

        @Test
        @DisplayName("해당 회원의 댓글을 조회할 수 있다.")
        void _willSuccess() throws Exception {
            // given
            List<CommentDTO> commentDTOList = new ArrayList<>();
            commentDTOList.add(
                CommentDTO.builder().id(0L).postId(0L).memberId(0L).content("test").anonymity(false)
                    .parentCommentId(null).build());
            given(commentService.getCommentsByMemberId(any(long.class))).willReturn(commentDTOList);

            // when, then
            mockMvc.perform(get("/api/v1/comment/my-page/0")).andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].id").exists())
                .andExpect(jsonPath("$.data[0].postId").exists())
                .andExpect(jsonPath("$.data[0].memberId").exists())
                .andExpect(jsonPath("$.data[0].content").exists())
                .andExpect(jsonPath("$.data[0].anonymity").exists())
                .andExpect(jsonPath("$.data[0].parentCommentId").isEmpty()).andDo(print());

            verify(commentService, times(1)).getCommentsByMemberId(any(long.class));
        }
    }

    @Nested
    @DisplayName("getCommentByPostId()은 ")
    class Context_getCommentByPostId {

        @Test
        @DisplayName("해당 게시물의 댓글을 조회할 수 있다.")
        void _willSuccess() throws Exception {
            // given
            List<CommentDTO> commentDTOList = new ArrayList<>();
            commentDTOList.add(
                CommentDTO.builder().id(0L).postId(0L).memberId(0L).content("test").anonymity(false)
                    .parentCommentId(null).build());
            given(commentService.getCommentsByPostId(any(long.class))).willReturn(commentDTOList);

            // when, then
            mockMvc.perform(get("/api/v1/comment/0")).andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].id").exists())
                .andExpect(jsonPath("$.data[0].postId").exists())
                .andExpect(jsonPath("$.data[0].memberId").exists())
                .andExpect(jsonPath("$.data[0].content").exists())
                .andExpect(jsonPath("$.data[0].anonymity").exists())
                .andExpect(jsonPath("$.data[0].parentCommentId").isEmpty()).andDo(print());

            verify(commentService, times(1)).getCommentsByPostId(any(long.class));
        }
    }

    @Nested
    @DisplayName("update()는 ")
    class Context_update {

        @Test
        @DisplayName("댓글을 수정할 수 있다.")
        void _willSuccess() throws Exception {
            // given
            UpdateCommentRequest request = UpdateCommentRequest.builder().id(0L).content("modified")
                .build();
            CommentDTO commentDto = CommentDTO.builder().id(0L).postId(0L).memberId(0L)
                .content("modified").anonymity(false).parentCommentId(null).build();
            given(commentService.updateComment(any(UpdateCommentRequest.class))).willReturn(
                commentDto);
            String json = new ObjectMapper().writeValueAsString(request);

            // when, then
            mockMvc.perform(
                    patch("/api/v1/comment").content(json).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andExpect(jsonPath("$.data.id").exists())
                .andExpect(jsonPath("$.data.postId").exists())
                .andExpect(jsonPath("$.data.memberId").exists())
                .andExpect(jsonPath("$.data.content").exists())
                .andExpect(jsonPath("$.data.anonymity").exists())
                .andExpect(jsonPath("$.data.parentCommentId").isEmpty()).andDo(print());

            verify(commentService, times(1)).updateComment(any(UpdateCommentRequest.class));
        }

        @Nested
        @DisplayName("id가 ")
        class Element_id {

            @Test
            @DisplayName("null일 경우 댓글을 수정할 수 없다.")
            void null_willFail() throws Exception {
                // given
                UpdateCommentRequest request = UpdateCommentRequest.builder().id(null)
                    .content("modified").build();
                CommentDTO commentDto = CommentDTO.builder().id(0L).postId(0L).memberId(0L)
                    .content("test").anonymity(false).parentCommentId(null).build();
                given(commentService.updateComment(any(UpdateCommentRequest.class))).willReturn(
                    commentDto);
                String json = new ObjectMapper().writeValueAsString(request);

                // when, then
                mockMvc.perform(
                        patch("/api/v1/comment").content(json).contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest()).andExpect(jsonPath("$.message").exists())
                    .andDo(print());

                verify(commentService, never()).updateComment(any(UpdateCommentRequest.class));
            }
        }

        @Nested
        @DisplayName("content가 ")
        class Element_content {

            @Test
            @DisplayName("null일 경우 댓글을 수정할 수 없다.")
            void null_willFail() throws Exception {
                // given
                UpdateCommentRequest request = UpdateCommentRequest.builder().id(0L).content(null)
                    .build();
                CommentDTO commentDto = CommentDTO.builder().id(0L).postId(0L).memberId(0L)
                    .content("test").anonymity(false).parentCommentId(null).build();
                given(commentService.updateComment(any(UpdateCommentRequest.class))).willReturn(
                    commentDto);
                String json = new ObjectMapper().writeValueAsString(request);

                // when, then
                mockMvc.perform(
                        patch("/api/v1/comment").content(json).contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest()).andExpect(jsonPath("$.message").exists())
                    .andDo(print());

                verify(commentService, never()).updateComment(any(UpdateCommentRequest.class));
            }

            @Test
            @DisplayName("빈 칸일 경우 댓글을 수정할 수 없다.")
            void blank_willFail() throws Exception {
                // given
                UpdateCommentRequest request = UpdateCommentRequest.builder().id(0L).content(" ")
                    .build();
                CommentDTO commentDto = CommentDTO.builder().id(0L).postId(0L).memberId(0L)
                    .content("test").anonymity(false).parentCommentId(null).build();
                given(commentService.updateComment(any(UpdateCommentRequest.class))).willReturn(
                    commentDto);
                String json = new ObjectMapper().writeValueAsString(request);

                // when, then
                mockMvc.perform(
                        patch("/api/v1/comment").content(json).contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest()).andExpect(jsonPath("$.message").exists())
                    .andDo(print());

                verify(commentService, never()).updateComment(any(UpdateCommentRequest.class));
            }
        }
    }

    @Nested
    @DisplayName("delete()은 ")
    class Context_delete {

        @Test
        @DisplayName("댓글을 삭제할 수 있다.")
        void _willSuccess() throws Exception {
            // given
            DeleteCommentRequest request = DeleteCommentRequest.builder().id(0L).build();
            CommentDTO commentDto = CommentDTO.builder().id(0L).postId(0L).memberId(0L)
                .content("test").anonymity(false).parentCommentId(null).build();
            given(commentService.deleteComment(any(DeleteCommentRequest.class))).willReturn(
                commentDto);
            String json = new ObjectMapper().writeValueAsString(request);

            // when, then
            mockMvc.perform(
                    delete("/api/v1/comment").content(json).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andExpect(jsonPath("$.data.id").exists())
                .andExpect(jsonPath("$.data.postId").exists())
                .andExpect(jsonPath("$.data.memberId").exists())
                .andExpect(jsonPath("$.data.content").exists())
                .andExpect(jsonPath("$.data.anonymity").exists())
                .andExpect(jsonPath("$.data.parentCommentId").isEmpty()).andDo(print());

            verify(commentService, times(1)).deleteComment(any(DeleteCommentRequest.class));
        }

        @Nested
        @DisplayName("id가 ")
        class Element_id {

            @Test
            @DisplayName("null일 경우 댓글을 삭제할 수 없다.")
            void null_willFail() throws Exception {
                // given
                DeleteCommentRequest request = DeleteCommentRequest.builder().id(null).build();
                CommentDTO commentDto = CommentDTO.builder().id(0L).postId(0L).memberId(0L)
                    .content("test").anonymity(false).parentCommentId(null).build();
                given(commentService.deleteComment(any(DeleteCommentRequest.class))).willReturn(
                    commentDto);
                String json = new ObjectMapper().writeValueAsString(request);

                // when, then
                mockMvc.perform(
                        delete("/api/v1/comment").content(json).contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest()).andExpect(jsonPath("$.message").exists())
                    .andDo(print());

                verify(commentService, never()).deleteComment(any(DeleteCommentRequest.class));
            }
        }
    }
}
