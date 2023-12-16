package com.fasttime.domain.member.unit.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;

import com.fasttime.domain.article.dto.service.response.ArticleResponse;
import com.fasttime.domain.article.dto.service.response.ArticlesResponse;
import com.fasttime.domain.article.entity.Article;
import com.fasttime.domain.article.entity.ReportStatus;
import com.fasttime.domain.article.exception.ArticleNotFoundException;
import com.fasttime.domain.article.exception.BadArticleReportStatusException;
import com.fasttime.domain.article.repository.ArticleRepository;
import com.fasttime.domain.article.service.ArticleQueryService;
import com.fasttime.domain.member.entity.Member;
import com.fasttime.domain.member.service.AdminService;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@ExtendWith(MockitoExtension.class)
public class AdminServiceTest {

    @InjectMocks
    private AdminService adminService;

    @Mock
    private ArticleRepository articleRepository;

    @Mock
    private ArticleQueryService articleQueryService;

    @DisplayName("findReportedPost()는 ")
    @Nested
    class PostList {

        @DisplayName("신고된 게시물들을 조회할 수 있다. ")
        @Test
        void _willSuccess() {
            //given
            ArticlesResponse articlesResponse1 = ArticlesResponse.builder()
                .id(1L)
                .title("testTitle1")
                .isAnonymity(true)
                .commentCounts(1)
                .likeCount(0)
                .hateCount(0)
                .createdAt(LocalDateTime.now())
                .lastModifiedAt(LocalDateTime.now())
                .build();
            ArticlesResponse articlesResponse2 = ArticlesResponse.builder()
                .id(1L)
                .title("testTitle2")
                .isAnonymity(true)
                .commentCounts(1)
                .likeCount(0)
                .hateCount(0)
                .createdAt(LocalDateTime.now())
                .lastModifiedAt(LocalDateTime.now())
                .build();

            List<ArticlesResponse> articlesResponses = new ArrayList<>();
            articlesResponses.add(articlesResponse1);
            articlesResponses.add(articlesResponse2);

            given(articleQueryService.findReportedArticles(any())).willReturn(articlesResponses);

            // when
            List<ArticlesResponse> articleList = adminService.findReportedPost(0);

            //then
            Assertions.assertThat(articleList.get(0).title()).isEqualTo("testTitle1");
            Assertions.assertThat(articleList.get(1).title()).isEqualTo("testTitle2");
        }
    }




    @DisplayName("findOneReportedPost()는")
    @Nested
    class PostDetail {

        @DisplayName("신고된 게시글을 조회 할 수 있다.")
        @Test
        void _willSuccess() {
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
            given(articleRepository.findById(anyLong())).willReturn(Optional.of(article1));
            //when
            ArticleResponse oneReportedPost = adminService.findOneReportedPost(1L);

            //then
            Assertions.assertThat(oneReportedPost.title()).isEqualTo(article1.getTitle());
        }

        @DisplayName("신고된 게시글이 존재하지 않아 조회 할 수 없다.")
        @Test
        void NotFound_willFail() {
            //given
            given(articleRepository.findById(anyLong())).willThrow(new ArticleNotFoundException());

            //when, then
            Assertions.assertThatThrownBy(() -> adminService.findOneReportedPost(1L)).isInstanceOf(
                ArticleNotFoundException.class);
        }

        @DisplayName("게시글이 신고된 상태가 아니라 조회 할 수 없다.")
        @Test
        void NoReported_willFail() {
            //given
            Member member = Member.builder().id(1L).email("testEmail").password("testPassword")
                .nickname("testNickname").build();

            Article article1 = Article.builder()
                .id(1L)
                .member(member)
                .title("testTitle1")
                .anonymity(true)
                .reportStatus(ReportStatus.NORMAL)
                .likeCount(0)
                .hateCount(0)
                .build();
            given(articleRepository.findById(anyLong())).willReturn(Optional.of(article1));
            //when ,then
            Assertions.assertThatThrownBy(() -> adminService.findOneReportedPost(1L)).isInstanceOf(
                BadArticleReportStatusException.class).hasMessage("신고 후처리를 할 수 없습니다.");

        }
    }

}
