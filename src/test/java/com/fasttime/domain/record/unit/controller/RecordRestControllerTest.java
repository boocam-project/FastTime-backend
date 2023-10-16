package com.fasttime.domain.record.unit.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasttime.domain.record.controller.RecordRestController;
import com.fasttime.domain.record.dto.RecordDTO;
import com.fasttime.domain.record.dto.request.CreateRecordRequestDTO;
import com.fasttime.domain.record.dto.request.DeleteRecordRequestDTO;
import com.fasttime.domain.record.service.RecordService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(RecordRestController.class)
public class RecordRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    RecordService recordService;

    @Nested
    @DisplayName("createLike()는")
    class Context_createLike {

        @Test
        @DisplayName("게시글을 좋아요 할 수 있다.")
        void _willSuccess() throws Exception {
            // given
            CreateRecordRequestDTO request = CreateRecordRequestDTO.builder().memberId(1L)
                .postId(1L).build();
            doNothing().when(recordService)
                .createRecord(any(CreateRecordRequestDTO.class), anyBoolean());
            String json = new ObjectMapper().writeValueAsString(request);

            // when, then
            mockMvc.perform(
                    post("/api/v1/record/like").content(json).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated()).andExpect(jsonPath("$.message").exists())
                .andDo(print());
        }

        @Nested
        @DisplayName("postId가 ")
        class Element_postId {

            @Test
            @DisplayName("null일 경우 좋아요 할 수 없다.")
            void null_willFail() throws Exception {
                // given
                CreateRecordRequestDTO request = CreateRecordRequestDTO.builder().memberId(1L)
                    .postId(null).build();
                doNothing().when(recordService)
                    .createRecord(any(CreateRecordRequestDTO.class), anyBoolean());
                String json = new ObjectMapper().writeValueAsString(request);

                // when, then
                mockMvc.perform(post("/api/v1/record/like").content(json)
                        .contentType(MediaType.APPLICATION_JSON)).andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").exists()).andDo(print());
            }
        }

        @Nested
        @DisplayName("memberId가 ")
        class Element_memberId {

            @Test
            @DisplayName("null일 경우 좋아요 할 수 없다.")
            void null_willFail() throws Exception {
                // given
                CreateRecordRequestDTO request = CreateRecordRequestDTO.builder().memberId(null)
                    .postId(1L).build();
                doNothing().when(recordService)
                    .createRecord(any(CreateRecordRequestDTO.class), anyBoolean());
                String json = new ObjectMapper().writeValueAsString(request);

                // when, then
                mockMvc.perform(post("/api/v1/record/like").content(json)
                        .contentType(MediaType.APPLICATION_JSON)).andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").exists()).andDo(print());
            }
        }
    }

    @Nested
    @DisplayName("createHate()는")
    class Context_createHate {

        @Test
        @DisplayName("게시글을 싫어요 할 수 있다.")
        void _willSuccess() throws Exception {
            // given
            CreateRecordRequestDTO request = CreateRecordRequestDTO.builder().memberId(1L)
                .postId(1L).build();
            doNothing().when(recordService)
                .createRecord(any(CreateRecordRequestDTO.class), anyBoolean());
            String json = new ObjectMapper().writeValueAsString(request);

            // when, then
            mockMvc.perform(
                    post("/api/v1/record/hate").content(json).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated()).andExpect(jsonPath("$.message").exists())
                .andDo(print());
        }

        @Nested
        @DisplayName("postId가 ")
        class Element_postId {

            @Test
            @DisplayName("null일 경우 싫어요 할 수 없다.")
            void null_willFail() throws Exception {
                // given
                CreateRecordRequestDTO request = CreateRecordRequestDTO.builder().memberId(1L)
                    .postId(null).build();
                doNothing().when(recordService)
                    .createRecord(any(CreateRecordRequestDTO.class), anyBoolean());
                String json = new ObjectMapper().writeValueAsString(request);

                // when, then
                mockMvc.perform(post("/api/v1/record/hate").content(json)
                        .contentType(MediaType.APPLICATION_JSON)).andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").exists()).andDo(print());
            }
        }

        @Nested
        @DisplayName("memberId가 ")
        class Element_memberId {

            @Test
            @DisplayName("null일 경우 싫어요 할 수 없다.")
            void null_willFail() throws Exception {
                // given
                CreateRecordRequestDTO request = CreateRecordRequestDTO.builder().memberId(null)
                    .postId(1L).build();
                doNothing().when(recordService)
                    .createRecord(any(CreateRecordRequestDTO.class), anyBoolean());
                String json = new ObjectMapper().writeValueAsString(request);

                // when, then
                mockMvc.perform(post("/api/v1/record/hate").content(json)
                        .contentType(MediaType.APPLICATION_JSON)).andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").exists()).andDo(print());
            }
        }
    }

    @Nested
    @DisplayName("getRecord()는")
    class Context_getRecord {

        @Test
        @DisplayName("좋아요/싫어요 데이터를 불러올 수 있다.")
        void _willSuccess() throws Exception {
            // given
            MockHttpSession session = new MockHttpSession();
            session.setAttribute("MEMBER", 1L);
            RecordDTO recordDTO = RecordDTO.builder().id(1L).memberId(1L).postId(1L).isLike(true)
                .build();
            given(recordService.getRecord(any(Long.class), any(Long.class))).willReturn(recordDTO);

            // when, then
            mockMvc.perform(get("/api/v1/record/{postId}", 1L).session(session)
                    .contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
                .andExpect(jsonPath("$.code").exists()).andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.data.id").exists())
                .andExpect(jsonPath("$.data.memberId").exists())
                .andExpect(jsonPath("$.data.postId").exists())
                .andExpect(jsonPath("$.data.isLike").exists()).andDo(print());
        }
    }

    @Nested
    @DisplayName("deleteRecord()는")
    class Context_deleteRecord {

        @Test
        @DisplayName("게시글 좋아요(싫어요)를 취소할 수 있다.")
        void _willSuccess() throws Exception {
            // given
            DeleteRecordRequestDTO request = DeleteRecordRequestDTO.builder().memberId(1L)
                .postId(1L).build();
            doNothing().when(recordService).deleteRecord(any(DeleteRecordRequestDTO.class));
            String json = new ObjectMapper().writeValueAsString(request);

            // when, then
            mockMvc.perform(
                    delete("/api/v1/record").content(json).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andExpect(jsonPath("$.message").exists())
                .andDo(print());
        }

        @Nested
        @DisplayName("postId가 ")
        class Element_postId {

            @Test
            @DisplayName("null일 경우 취소할 수 없다.")
            void null_willFail() throws Exception {
                // given
                DeleteRecordRequestDTO request = DeleteRecordRequestDTO.builder().memberId(1L)
                    .postId(null).build();
                doNothing().when(recordService).deleteRecord(any(DeleteRecordRequestDTO.class));
                String json = new ObjectMapper().writeValueAsString(request);

                // when, then
                mockMvc.perform(
                        delete("/api/v1/record").content(json).contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest()).andExpect(jsonPath("$.message").exists())
                    .andDo(print());
            }
        }

        @Nested
        @DisplayName("memberId가 ")
        class Element_memberId {

            @Test
            @DisplayName("null일 경우 취소할 수 없다.")
            void null_willFail() throws Exception {
                // given
                DeleteRecordRequestDTO request = DeleteRecordRequestDTO.builder().memberId(null)
                    .postId(1L).build();
                doNothing().when(recordService).deleteRecord(any(DeleteRecordRequestDTO.class));
                String json = new ObjectMapper().writeValueAsString(request);

                // when, then
                mockMvc.perform(
                        delete("/api/v1/record").content(json).contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest()).andExpect(jsonPath("$.message").exists())
                    .andDo(print());
            }
        }
    }
}
