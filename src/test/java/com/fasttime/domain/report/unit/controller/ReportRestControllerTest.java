package com.fasttime.domain.report.unit.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasttime.domain.report.contoller.ReportRestController;
import com.fasttime.domain.report.dto.request.CreateReportRequestDTO;
import com.fasttime.domain.report.service.ReportService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(ReportRestController.class)
public class ReportRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    ReportService reportService;

    @Nested
    @DisplayName("createReport()는")
    class Context_createReport {

        @Test
        @DisplayName("게시글을 신고할 수 있다.")
        void _willSuccess() throws Exception {
            // given
            CreateReportRequestDTO request = CreateReportRequestDTO.builder().postId(1L).build();
            doNothing().when(reportService)
                .createReport(any(CreateReportRequestDTO.class), any(Long.class));
            String json = new ObjectMapper().writeValueAsString(request);
            MockHttpSession session = new MockHttpSession();
            session.setAttribute("MEMBER", 1L);

            // when, then
            mockMvc.perform(
                    post("/api/v1/report").content(json).contentType(MediaType.APPLICATION_JSON)
                        .session(session)).andExpect(status().isCreated())
                .andExpect(jsonPath("$.code").exists()).andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.data").isEmpty()).andDo(print());
        }

        @Nested
        @DisplayName("postId가 ")
        class Element_postId {

            @Test
            @DisplayName("null일 경우 신고할 수 없다.")
            void null_willFail() throws Exception {
                // given
                CreateReportRequestDTO request = CreateReportRequestDTO.builder().postId(null).build();
                doNothing().when(reportService)
                    .createReport(any(CreateReportRequestDTO.class), any(Long.class));
                String json = new ObjectMapper().writeValueAsString(request);
                MockHttpSession session = new MockHttpSession();
                session.setAttribute("MEMBER", 1L);

                // when, then
                mockMvc.perform(post("/api/v1/report").content(json)
                        .contentType(MediaType.APPLICATION_JSON)).andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.code").exists())
                    .andExpect(jsonPath("$.message").exists())
                    .andExpect(jsonPath("$.data").isEmpty()).andDo(print());
            }
        }
    }
}
