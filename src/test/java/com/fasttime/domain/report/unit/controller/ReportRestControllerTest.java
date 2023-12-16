package com.fasttime.domain.report.unit.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasttime.domain.report.dto.request.CreateReportRequestDTO;
import com.fasttime.util.ControllerUnitTestSupporter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;

public class ReportRestControllerTest extends ControllerUnitTestSupporter {

    @Nested
    @DisplayName("createReport()는")
    class Context_createReport {

        @Test
        @DisplayName("게시글을 신고할 수 있다.")
        void _willSuccess() throws Exception {
            // given
            CreateReportRequestDTO request = CreateReportRequestDTO.builder().articleId(1L).build();
            doNothing().when(reportService)
                .createReport(any(CreateReportRequestDTO.class), any(Long.class));
            String json = new ObjectMapper().writeValueAsString(request);
            MockHttpSession session = new MockHttpSession();
            session.setAttribute("MEMBER", 1L);

            // when, then
            mockMvc.perform(
                    post("/api/v1/report")
                        .content(json)
                        .contentType(MediaType.APPLICATION_JSON)
                        .session(session))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.code").exists()).andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.data").isEmpty()).andDo(print());
        }

        @Nested
        @DisplayName("articleId가 ")
        class Element_articleId {

            @Test
            @DisplayName("null일 경우 신고할 수 없다.")
            void null_willFail() throws Exception {
                // given
                CreateReportRequestDTO request = CreateReportRequestDTO.builder().articleId(null)
                    .build();
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
