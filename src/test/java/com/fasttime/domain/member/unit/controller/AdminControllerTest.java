package com.fasttime.domain.member.unit.controller;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasttime.domain.article.dto.service.response.ArticleResponse;
import com.fasttime.domain.article.dto.service.response.ArticlesResponse;
import com.fasttime.domain.article.entity.Article;
import com.fasttime.domain.article.entity.ReportStatus;
import com.fasttime.domain.article.exception.ArticleNotFoundException;
import com.fasttime.domain.article.exception.BadArticleReportStatusException;
import com.fasttime.domain.member.entity.Member;
import com.fasttime.util.ControllerUnitTestSupporter;
import java.util.ArrayList;
import java.util.List;
import org.apache.catalina.security.SecurityConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

public class AdminControllerTest extends ControllerUnitTestSupporter {

    @DisplayName("findReportedPost()는 ")
    @Nested
    class PostList {

        @DisplayName("신고된 게시물들을 조회할 수 있다. ")
        @Test
        void _willSuccess() throws Exception {
            //given

            ArticlesResponse article1 = ArticlesResponse.builder()
                .id(1L)
                .title("testTitle1")
                .nickname("땅땅띠라랑")
                .isAnonymity(true)
                .likeCount(0)
                .hateCount(0)
                .build();
            ArticlesResponse article2 = ArticlesResponse.builder()
                .id(2L)
                .nickname("땅땅띠라랑")
                .title("testTitle2")
                .isAnonymity(true)
                .likeCount(0)
                .hateCount(0)
                .build();
            List<ArticlesResponse> articles = new ArrayList<>();
            articles.add(article1);
            articles.add(article2);

            when(adminService.findReportedPost(anyInt())).thenReturn(articles);

            // when, then
            mockMvc.perform(get("/api/v1/admin/reports/articles"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("신고가 10번이상된 게시글들을 보여줍니다."))
                .andExpect(jsonPath("$.data").exists())
                .andDo(print());
        }
    }

    @DisplayName("findOneReportedPost()는")
    @Nested
    class PostDetail {

        @DisplayName("신고된 게시글을 조회 할 수 있다.")
        @Test
        void _willSuccess() throws Exception {
            //given

            ArticleResponse article1 = ArticleResponse.builder()
                .id(1L)
                .title("testTitle1")
                .nickname("땅땅띠라랑")
                .isAnonymity(true)
                .likeCount(0)
                .hateCount(0)
                .build();

            when(adminService.findOneReportedPost(anyLong())).thenReturn(article1);
            //when, then
            mockMvc.perform(get("/api/v1/admin/{article_id}", article1.id()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").exists())
                .andExpect(jsonPath("$.data.title").exists())
                .andExpect(jsonPath("$.data.nickname").exists())
                .andDo(print());
        }

        @DisplayName("신고된 게시글이 존재하지 않아 조회 할 수 없다.")
        @Test
        void NotFound_willFail() throws Exception {
            //given

            when(adminService.findOneReportedPost(anyLong())).thenThrow(
                new ArticleNotFoundException());

            //when, then
            mockMvc.perform(get("/api/v1/admin/{article_id}", 1L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("존재하지 않는 게시글입니다."))
                .andDo(print());
        }

        @DisplayName("게시글이 신고된 상태가 아니라 조회 할 수 없다.")
        @Test
        void NoReported_willFail() throws Exception {
            //given

            when(adminService.findOneReportedPost(anyLong())).thenThrow(
                new BadArticleReportStatusException());

            //when ,then
            mockMvc.perform(get("/api/v1/admin/{article_id}", 1L))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("신고 후처리를 할 수 없습니다."))
                .andDo(print());

        }
    }

    @DisplayName("deletePost()는")
    @Nested
    class PostDelete {

        @DisplayName("삭제할 수 있다.")
        @Test
        void Delete_willSuccess() throws Exception {
            //given
            Member member = Member.builder().id(1L).email("testEmail").password("testPassword")
                .nickname("testNickname").build();

            Article article1 = Article.builder()
                .id(1L)
                .member(member)
                .title("testTitle1")
                .anonymity(true)
                .reportStatus(ReportStatus.WAIT_FOR_REPORT_REVIEW)
                .likeCount(0)
                .hateCount(0)
                .build();

            doNothing().when(adminService).deletePost(anyLong());

            //when, then
            mockMvc.perform(get("/api/v1/admin/{article_id}/delete", article1.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message")
                    .value("신고가 10번이상된 게시글을 삭제합니다."))
                .andDo(print());
        }
    }

    @DisplayName("passPost()는")
    @Nested
    class PostPass {

        @DisplayName("검토완료로 바꿀 수 있다.")
        @Test
        void Pass_willSuccess() throws Exception {
            //given
            Member member = Member.builder().id(1L).email("testEmail").password("testPassword")
                .nickname("testNickname").build();

            Article article1 = Article.builder()
                .id(1L)
                .member(member)
                .title("testTitle1")
                .anonymity(true)
                .reportStatus(ReportStatus.WAIT_FOR_REPORT_REVIEW)
                .likeCount(0)
                .hateCount(0)
                .build();

            doNothing().when(adminService).passPost(anyLong());
            //when, then
            mockMvc.perform(get("/api/v1/admin/{article_id}/pass", article1.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message")
                    .value("신고가 10번이상된 게시글을 복구합니다."))
                .andDo(print());
        }
    }

}