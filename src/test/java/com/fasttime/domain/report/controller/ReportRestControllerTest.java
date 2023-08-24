package com.fasttime.domain.report.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasttime.domain.report.contoller.ReportRestController;
import com.fasttime.domain.report.dto.ReportDTO;
import com.fasttime.domain.report.dto.request.CreateReportRequest;
import com.fasttime.domain.report.service.ReportService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(ReportRestController.class)
public class ReportRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    ReportService reportService;

    @Nested
    @DisplayName("create()은")
    class Context_create {

        @Test
        @DisplayName("게시글을 신고할 수 있다.")
        void _willSuccess() throws Exception {

            // given
            CreateReportRequest request = CreateReportRequest.builder().postId(0L).memberId(0L)
                .build();
            ReportDTO reportDTO = ReportDTO.builder().id(0L).postId(0L).memberId(0L).build();
            given(reportService.createReport(any(CreateReportRequest.class))).willReturn(reportDTO);
            String json = new ObjectMapper().writeValueAsString(request);

            // when, then
            mockMvc.perform(
                    post("/api/v1/report/create").content(json).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated()).andExpect(jsonPath("$.data.id").exists())
                .andExpect(jsonPath("$.data.postId").exists())
                .andExpect(jsonPath("$.data.memberId").exists()).andDo(print());
            verify(reportService, times(1)).createReport(any(CreateReportRequest.class));
        }

        @Nested
        @DisplayName("postId가 ")
        class Element_postId {

            @Test
            @DisplayName("null일 경우 신고할 수 없다.")
            void null_willFail() throws Exception {

                // given
                CreateReportRequest request = CreateReportRequest.builder().postId(null)
                    .memberId(0L).build();
                ReportDTO reportDto = ReportDTO.builder().id(0L).postId(0L).memberId(0L).build();
                given(reportService.createReport(any(CreateReportRequest.class))).willReturn(
                    reportDto);
                String json = new ObjectMapper().writeValueAsString(request);

                // when, then
                mockMvc.perform(post("/api/v1/report/create").content(json)
                        .contentType(MediaType.APPLICATION_JSON)).andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").exists()).andDo(print());
                verify(reportService, never()).createReport(any(CreateReportRequest.class));
            }
        }

        @Nested
        @DisplayName("memberId가 ")
        class Element_memberId {

            @Test
            @DisplayName("null일 경우 댓글을 등록할 수 없다.")
            void null_willFail() throws Exception {

                // given
                CreateReportRequest request = CreateReportRequest.builder().postId(0L)
                    .memberId(null).build();
                ReportDTO reportDto = ReportDTO.builder().id(0L).postId(0L).memberId(0L).build();
                given(reportService.createReport(any(CreateReportRequest.class))).willReturn(
                    reportDto);
                String json = new ObjectMapper().writeValueAsString(request);

                // when, then
                mockMvc.perform(post("/api/v1/report/create").content(json)
                        .contentType(MediaType.APPLICATION_JSON)).andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").exists()).andDo(print());
                verify(reportService, never()).createReport(any(CreateReportRequest.class));
            }
        }
    }
}
