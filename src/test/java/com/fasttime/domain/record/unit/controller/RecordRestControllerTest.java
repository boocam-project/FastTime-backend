package com.fasttime.domain.record.unit.controller;

import static org.mockito.ArgumentMatchers.any;
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
    @DisplayName("createRecord()는")
    class Context_createRecord {

        @Test
        @DisplayName("게시글을 좋아요 할 수 있다.")
        void like_willSuccess() throws Exception {
            // given
            CreateRecordRequestDTO request = CreateRecordRequestDTO.builder().articleId(1L)
                .isLike(true).build();
            doNothing().when(recordService)
                .createRecord(any(CreateRecordRequestDTO.class), any(Long.class));
            String json = new ObjectMapper().writeValueAsString(request);
            MockHttpSession session = new MockHttpSession();
            session.setAttribute("MEMBER", 1L);

            // when, then
            mockMvc.perform(
                    post("/api/v1/record").content(json).contentType(MediaType.APPLICATION_JSON)
                        .session(session)).andExpect(status().isCreated())
                .andExpect(jsonPath("$.code").exists()).andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.data").isEmpty()).andDo(print());
        }

        @Test
        @DisplayName("게시글을 싫어요 할 수 있다.")
        void hate_willSuccess() throws Exception {
            // given
            CreateRecordRequestDTO request = CreateRecordRequestDTO.builder().articleId(1L)
                .isLike(false).build();
            doNothing().when(recordService)
                .createRecord(any(CreateRecordRequestDTO.class), any(Long.class));
            String json = new ObjectMapper().writeValueAsString(request);
            MockHttpSession session = new MockHttpSession();
            session.setAttribute("MEMBER", 1L);

            // when, then
            mockMvc.perform(
                    post("/api/v1/record").content(json).contentType(MediaType.APPLICATION_JSON)
                        .session(session)).andExpect(status().isCreated())
                .andExpect(jsonPath("$.code").exists()).andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.data").isEmpty()).andDo(print());
        }

        @Nested
        @DisplayName("articleId가 ")
        class Element_articleId {

            @Test
            @DisplayName("null일 경우 좋아요/싫어요를 등록할 수 없다.")
            void null_willFail() throws Exception {
                // given
                CreateRecordRequestDTO request = CreateRecordRequestDTO.builder().articleId(null)
                    .isLike(true).build();
                doNothing().when(recordService)
                    .createRecord(any(CreateRecordRequestDTO.class), any(Long.class));
                String json = new ObjectMapper().writeValueAsString(request);
                MockHttpSession session = new MockHttpSession();
                session.setAttribute("MEMBER", 1L);

                // when, then
                mockMvc.perform(
                        post("/api/v1/record").content(json).contentType(MediaType.APPLICATION_JSON)
                            .session(session)).andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.code").exists())
                    .andExpect(jsonPath("$.message").exists())
                    .andExpect(jsonPath("$.data").isEmpty()).andDo(print());
            }
        }

        @Nested
        @DisplayName("isLike가 ")
        class Element_isLike {

            @Test
            @DisplayName("null일 경우 좋아요/싫어요를 등록할 수 없다.")
            void null_willFail() throws Exception {
                // given
                CreateRecordRequestDTO request = CreateRecordRequestDTO.builder().articleId(1L)
                    .isLike(null).build();
                doNothing().when(recordService)
                    .createRecord(any(CreateRecordRequestDTO.class), any(Long.class));
                String json = new ObjectMapper().writeValueAsString(request);
                MockHttpSession session = new MockHttpSession();
                session.setAttribute("MEMBER", 1L);

                // when, then
                mockMvc.perform(
                        post("/api/v1/record").content(json).contentType(MediaType.APPLICATION_JSON)
                            .session(session)).andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.code").exists())
                    .andExpect(jsonPath("$.message").exists())
                    .andExpect(jsonPath("$.data").isEmpty()).andDo(print());
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
            RecordDTO recordDTO = RecordDTO.builder().id(1L).memberId(1L).articleId(1L).isLike(true)
                .build();
            given(recordService.getRecord(any(Long.class), any(Long.class))).willReturn(recordDTO);
            MockHttpSession session = new MockHttpSession();
            session.setAttribute("MEMBER", 1L);

            // when, then
            mockMvc.perform(get("/api/v1/record/{articleId}", 1L).session(session)
                    .contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
                .andExpect(jsonPath("$.code").exists()).andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.data").isMap()).andExpect(jsonPath("$.data.id").exists())
                .andExpect(jsonPath("$.data.memberId").exists())
                .andExpect(jsonPath("$.data.articleId").exists())
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
            DeleteRecordRequestDTO request = DeleteRecordRequestDTO.builder().articleId(1L).build();
            doNothing().when(recordService)
                .deleteRecord(any(DeleteRecordRequestDTO.class), any(Long.class));
            String json = new ObjectMapper().writeValueAsString(request);
            MockHttpSession session = new MockHttpSession();
            session.setAttribute("MEMBER", 1L);

            // when, then
            mockMvc.perform(
                    delete("/api/v1/record").content(json).contentType(MediaType.APPLICATION_JSON)
                        .session(session)).andExpect(status().isOk())
                .andExpect(jsonPath("$.code").exists()).andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.data").isEmpty()).andDo(print());
        }

        @Nested
        @DisplayName("articleId가 ")
        class Element_articleId {

            @Test
            @DisplayName("null일 경우 취소할 수 없다.")
            void null_willFail() throws Exception {
                // given
                DeleteRecordRequestDTO request = DeleteRecordRequestDTO.builder().articleId(null)
                    .build();
                doNothing().when(recordService)
                    .deleteRecord(any(DeleteRecordRequestDTO.class), any(Long.class));
                String json = new ObjectMapper().writeValueAsString(request);
                MockHttpSession session = new MockHttpSession();
                session.setAttribute("MEMBER", 1L);

                // when, then
                mockMvc.perform(
                        delete("/api/v1/record").content(json).contentType(MediaType.APPLICATION_JSON)
                            .session(session)).andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.code").exists())
                    .andExpect(jsonPath("$.message").exists())
                    .andExpect(jsonPath("$.data").isEmpty()).andDo(print());
            }
        }
    }
}
