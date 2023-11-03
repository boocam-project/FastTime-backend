package com.fasttime.domain.member.unit.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasttime.domain.member.dto.MemberDto;
import com.fasttime.domain.member.repository.MemberRepository;
import com.fasttime.domain.member.service.MemberService;
import com.fasttime.domain.article.dto.service.response.ArticleResponse;
import com.fasttime.domain.article.entity.Article;
import com.fasttime.domain.article.repository.ArticleRepository;
import com.fasttime.domain.article.service.ArticleCommandService;
import com.fasttime.domain.article.service.usecase.ArticleCommandUseCase.ArticleCreateServiceRequest;
import java.time.LocalDateTime;
import javax.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
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
    private ArticleCommandService postCommandService;
    @Autowired
    private ArticleRepository postRepository;
    @Autowired
    private MemberService memberService;
    @Autowired
    private MemberRepository memberRepository;

    @BeforeEach
    void addMember(){
        memberService.save(new MemberDto("test","test","test"));
    }

    @DisplayName("신고된 게시물들을(이)")
    @Nested
    class PostList{
        @DisplayName("조회할 수 있다. ")
        @Test
        void _willSuccess() throws Exception {
            //given
            ArticleCreateServiceRequest dto1 = new ArticleCreateServiceRequest
                (memberRepository.findByEmail("test").get().getId(),
                    "testTitle1", "testContent1", false);
            ArticleCreateServiceRequest dto2 = new ArticleCreateServiceRequest
                (memberRepository.findByEmail("test").get().getId(), "testTitle2", "testContent2", false);

            ArticleResponse result1 = postCommandService.write(dto1);
            ArticleResponse result2 = postCommandService.write(dto2);
            Article postFromDB1 = postRepository.findById(result1.getId()).get();
            Article postFromDB2 = postRepository.findById(result2.getId()).get();
            postFromDB1.transToWaitForReview();
            postFromDB2.transToWaitForReview();
            postFromDB1.approveReport(LocalDateTime.now());
            postFromDB2.approveReport(LocalDateTime.now());

            // when, then
            mockMvc.perform(get("/api/v1/admin"))
                .andExpect(jsonPath("$.data").exists())
                .andDo(print());
        }

        @DisplayName("없으면 조회 할 수 없다.")
        @Test
        void _willFail() throws Exception {
            //given
            ArticleCreateServiceRequest dto1 = new ArticleCreateServiceRequest
                (memberRepository.findByEmail("test").get().getId(),
                    "testTitle1", "testContent1", false);
            ArticleCreateServiceRequest dto2 = new ArticleCreateServiceRequest
                (memberRepository.findByEmail("test").get().getId(),
                    "testTitle2", "testContent2", false);
          
            ArticleResponse savedPost1 = postCommandService.write(dto1);
            ArticleResponse savedPost2 = postCommandService.write(dto2);
            Article postFromDB1 = postRepository.findById(savedPost1.getId()).get();
            Article postFromDB2 = postRepository.findById(savedPost2.getId()).get();

            // when, then
            mockMvc.perform(get("/api/v1/admin"))
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

            ArticleCreateServiceRequest dto1 = new ArticleCreateServiceRequest
                (memberRepository.findByEmail("test").get().getId(),
                    "testTitle1", "testContent1", false);

            ArticleResponse newPost = postCommandService.write(dto1);
            Article post1 = postRepository.findById(newPost.getId()).get();

            post1.transToWaitForReview();
            post1.approveReport(LocalDateTime.now());
            //when, then
            mockMvc.perform(get("/api/v1/admin/{article_id}", post1.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").exists())
                .andDo(print());
        }
        // IllegalArgumentException Test Case
        @DisplayName("URL 직접 접근으로 인해 조회할 수 없다.")
        @Test
        void _willFail() throws Exception {
            //given
            ArticleCreateServiceRequest dto1 = new ArticleCreateServiceRequest
                (memberRepository.findByEmail("test").get().getId(),
                    "testTitle1", "testContent1", false);

            ArticleResponse newPost = postCommandService.write(dto1);
            Article post1 = postRepository.findById(newPost.getId()).get();

            post1.transToWaitForReview();
            post1.approveReport(LocalDateTime.now());
            //when, then
            mockMvc.perform(get("/api/v1/admin/{post_id}", 1000L))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("게시글이 없습니다."))
                .andExpect(jsonPath("$.data").isEmpty())
                .andDo(print());
        }
        // AccessException
        @DisplayName("신고처리가 되지않는 게시글 접근으로 인해 조회할 수 없다.")
        @Test
        void Access_willFail() throws Exception {
            //given
            ArticleCreateServiceRequest dto1 = new ArticleCreateServiceRequest
                (memberRepository.findByEmail("test").get().getId(),
                    "testTitle1", "testContent1", false);

            ArticleResponse newPost = postCommandService.write(dto1);
            Article post1 = postRepository.findById(newPost.getId()).get();

            //when, then
            mockMvc.perform(get("/api/v1/admin/{post_id}",  post1.getId()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("잘못된 접근입니다."))
                .andExpect(jsonPath("$.data").isEmpty())
                .andDo(print());
        }

        @DisplayName("삭제할 수 있다.")
        @Test
        void Delete_willSuccess() throws Exception {
            //given
            ArticleCreateServiceRequest dto1 = new ArticleCreateServiceRequest
                (memberRepository.findByEmail("test").get().getId(),
                    "testTitle1", "testContent1", false);

            ArticleResponse newPost = postCommandService.write(dto1);
            Article post1 = postRepository.findById(newPost.getId()).get();

            post1.transToWaitForReview();
            post1.approveReport(LocalDateTime.now());
            //when, then
            mockMvc.perform(get("/api/v1/admin/{post_id}/delete", post1.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message")
                    .value("신고가 10번이상된 게시글을 삭제합니다."))
                .andDo(print());
        }
        @DisplayName("검토완료로 바꿀 수 있다.")
        @Test
        void Pass_willSuccess() throws Exception {
            //given
            ArticleCreateServiceRequest dto1 = new ArticleCreateServiceRequest
                (memberRepository.findByEmail("test").get().getId(),
                    "testTitle1", "testContent1", false);

            ArticleResponse newPost = postCommandService.write(dto1);
            Article post1 = postRepository.findById(newPost.getId()).get();

            post1.transToWaitForReview();
            post1.approveReport(LocalDateTime.now());
            //when, then
            mockMvc.perform(get("/api/v1/admin/{post_id}/pass", post1.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message")
                    .value("신고가 10번이상된 게시글을 복구합니다."))
                .andDo(print());
        }
    }



}
