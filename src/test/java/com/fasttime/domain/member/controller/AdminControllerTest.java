package com.fasttime.domain.member.controller;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasttime.domain.member.service.AdminService;
import com.fasttime.domain.post.dto.service.request.PostCreateServiceDto;
import com.fasttime.domain.post.dto.service.response.PostResponseDto;
import com.fasttime.domain.post.entity.Post;
import com.fasttime.domain.post.entity.ReportStatus;
import com.fasttime.domain.post.repository.PostRepository;
import com.fasttime.domain.post.service.PostCommandService;
import javax.transaction.Transactional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@Transactional
@AutoConfigureMockMvc
public class AdminControllerTest {

    @Autowired
    MockMvc mockMvc;
    @Autowired
    private AdminService adminService;
    @Autowired
    private PostCommandService postCommandService;
    @Autowired
    private PostRepository postRepository;

    @DisplayName("신고된 게시물들을(이)")
    @Nested
    class PostList{
        @DisplayName("조회할 수 있다. ")
        @Test
        void _willSuccess() throws Exception {
            //given
            PostCreateServiceDto dto1 = new PostCreateServiceDto
                (0L, "testTitle1", "testContent1", false);
            PostCreateServiceDto dto2 = new PostCreateServiceDto
                (1L, "testTitle2", "testContent2", false);
            PostResponseDto postResponseDto1 = postCommandService.writePost(dto1);
            PostResponseDto postResponseDto2 = postCommandService.writePost(dto2);
            Post post1 = postRepository.findById(postResponseDto1.getId()).get();
            Post post2 = postRepository.findById(postResponseDto2.getId()).get();
            post1.ChangeReportStatus(ReportStatus.REPORTED);
            post2.ChangeReportStatus(ReportStatus.REPORTED);
            // when, then
            mockMvc.perform(get("/v1/admin"))
                .andExpect(jsonPath("$.data").exists())
                .andDo(print());

        }
        @DisplayName("없어 조회 할 수 없다.")
        @Test
        void _willFail() throws Exception {
            //given
            PostCreateServiceDto dto1 = new PostCreateServiceDto
                (0L, "testTitle1", "testContent1", false);
            PostCreateServiceDto dto2 = new PostCreateServiceDto
                (1L, "testTitle2", "testContent2", false);
            PostResponseDto postResponseDto1 = postCommandService.writePost(dto1);
            PostResponseDto postResponseDto2 = postCommandService.writePost(dto2);
            Post post1 = postRepository.findById(postResponseDto1.getId()).get();
            Post post2 = postRepository.findById(postResponseDto2.getId()).get();
            // when, then
            mockMvc.perform(get("/v1/admin"))
                .andExpect(jsonPath("$.data").isEmpty())
                .andDo(print());
        }
    }
    @DisplayName("게시물을")
    @Nested
    class PostDetail{
        @DisplayName("조회할 수 있다.")
        @Test
        void _willSuccess() throws Exception {
            //given
            PostCreateServiceDto dto1 = new PostCreateServiceDto
                (0L, "testTitle1", "testContent1", false);
            PostResponseDto postResponseDto1 = postCommandService.writePost(dto1);
            Post post1 = postRepository.findById(postResponseDto1.getId()).get();
            post1.ChangeReportStatus(ReportStatus.REPORTED);
            //when, then
            mockMvc.perform(get("/v1/admin/{post_id}", post1.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").exists())
                .andDo(print());
        }
        // IllegalArgumentException Test Case
        @DisplayName("URL 직접 접근으로 인해 조회할 수 없다.")
        @Test
        void _willFail() throws Exception {
            //given
            PostCreateServiceDto dto1 = new PostCreateServiceDto
                (0L, "testTitle1", "testContent1", false);
            PostResponseDto postResponseDto1 = postCommandService.writePost(dto1);
            Post post1 = postRepository.findById(postResponseDto1.getId()).get();
            post1.ChangeReportStatus(ReportStatus.REPORTED);
            //when, then
            mockMvc.perform(get("/v1/admin/{post_id}", 1000L))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").exists())
                .andDo(print());
        }

        @DisplayName("삭제할 수 있다.")
        @Test
        void Delete_willSuccess() throws Exception {
            //given
            PostCreateServiceDto dto1 = new PostCreateServiceDto
                (0L, "testTitle1", "testContent1", false);
            PostResponseDto postResponseDto1 = postCommandService.writePost(dto1);
            Post post1 = postRepository.findById(postResponseDto1.getId()).get();
            post1.ChangeReportStatus(ReportStatus.REPORTED);
            //when, then
            mockMvc.perform(get("/v1/admin/{post_id}/delete", post1.getId()))
                .andExpect(status().isOk())
                .andDo(print());
        }
        @DisplayName("검토완료로 바꿀 수 있다.")
        @Test
        void Pass_willSuccess() throws Exception {
            //given
            PostCreateServiceDto dto1 = new PostCreateServiceDto
                (0L, "testTitle1", "testContent1", false);
            PostResponseDto postResponseDto1 = postCommandService.writePost(dto1);
            Post post1 = postRepository.findById(postResponseDto1.getId()).get();
            post1.ChangeReportStatus(ReportStatus.REPORTED);
            //when, then
            mockMvc.perform(get("/v1/admin/{post_id}/pass", post1.getId()))
                .andExpect(status().isOk())
                .andDo(print());
        }
    }



}
