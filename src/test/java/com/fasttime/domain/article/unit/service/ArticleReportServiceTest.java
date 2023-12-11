package com.fasttime.domain.article.unit.service;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import com.fasttime.domain.article.entity.Article;
import com.fasttime.domain.article.entity.ReportStatus;
import com.fasttime.domain.article.exception.BadArticleReportStatusException;
import com.fasttime.domain.article.repository.ArticleRepository;
import com.fasttime.domain.article.service.ArticleReportService;
import com.fasttime.domain.article.service.usecase.ArticleReportUseCase.ArticleReportServiceRequest;
import com.fasttime.domain.member.entity.Member;
import java.time.LocalDateTime;
import java.util.Optional;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

class ArticleReportServiceTest {

    private final ArticleRepository articleRepository = mock(ArticleRepository.class);

    private final ArticleReportService articleReportService = new ArticleReportService(
        articleRepository);

    @DisplayName("reportArticle 은")
    @Nested
    class Context_reportArticle {

        @DisplayName("성공적으로 WAIT_FOR_REVIEW 상태로 전환할 수 있다.")
        @Test
        void changeArticleStatus_toWaitForReview_willSuccess() {

            // given
            LocalDateTime reportTimeStamp = LocalDateTime.now();
            Article article = Article.builder()
                .id(1L)
                .member(createSimpleMember(1L))
                .title("title")
                .content("content")
                .anonymity(true)
                .reportStatus(ReportStatus.NORMAL)
                .build();

            given(articleRepository.findById(anyLong())).willReturn(Optional.of(article));

            // when
            articleReportService.reportArticle(
                new ArticleReportServiceRequest(article.getId(), reportTimeStamp));

            // then
            Assertions.assertThat(article.getReportStatus())
                .isEqualTo(ReportStatus.WAIT_FOR_REPORT_REVIEW);
        }

        @DisplayName("만약 이미 NORMAL 상태가 아니라면 실패한다.")
        @EnumSource(value = ReportStatus.class, names = {"REPORT_ACCEPT", "REPORT_REJECT",
            "WAIT_FOR_REPORT_REVIEW"})
        @ParameterizedTest
        void reportStatus_alreadyAccept_willFail(ReportStatus reportStatus) {

            // given
            LocalDateTime reportTimeStamp = LocalDateTime.now();
            Article article = Article.builder()
                .id(1L)
                .member(createSimpleMember(1L))
                .title("title")
                .content("content")
                .anonymity(true)
                .reportStatus(reportStatus)
                .build();

            given(articleRepository.findById(anyLong())).willReturn(Optional.of(article));

            // when then
            Assertions.assertThatThrownBy(() -> articleReportService.reportArticle(
                    new ArticleReportServiceRequest(article.getId(), reportTimeStamp)))
                .isInstanceOf(BadArticleReportStatusException.class);
        }
    }

    @DisplayName("acceptReport 은")
    @Nested
    class Context_acceptReport {

        @DisplayName("성공적으로 REPORT_ACCEPT 상태로 전환할 수 있다.")
        @Test
        void changeArticleStatus_toReportAccept_willSuccess() {

            // given
            LocalDateTime reportTimeStamp = LocalDateTime.now();
            Article article = Article.builder()
                .id(1L)
                .member(createSimpleMember(1L))
                .title("title")
                .content("content")
                .anonymity(true)
                .reportStatus(ReportStatus.WAIT_FOR_REPORT_REVIEW)
                .build();

            given(articleRepository.findById(anyLong())).willReturn(Optional.of(article));

            // when
            articleReportService.acceptReport(
                new ArticleReportServiceRequest(article.getId(), reportTimeStamp));

            // then
            Assertions.assertThat(article.getReportStatus())
                .isEqualTo(ReportStatus.REPORT_ACCEPT);
        }

        @DisplayName("만약 이미 WAIT_FOR_REPORT_REVIEW 상태가 아니라면 실패한다.")
        @EnumSource(value = ReportStatus.class, names = {"REPORT_ACCEPT", "REPORT_REJECT",
            "NORMAL"})
        @ParameterizedTest
        void reportStatus_notWaitForReportReview_willFail(ReportStatus reportStatus) {

            // given
            LocalDateTime reportTimeStamp = LocalDateTime.now();
            Article article = Article.builder()
                .id(1L)
                .member(createSimpleMember(1L))
                .title("title")
                .content("content")
                .anonymity(true)
                .reportStatus(reportStatus)
                .build();

            given(articleRepository.findById(anyLong())).willReturn(Optional.of(article));

            // when then
            Assertions.assertThatThrownBy(() -> articleReportService.acceptReport(
                    new ArticleReportServiceRequest(article.getId(), reportTimeStamp)))
                .isInstanceOf(BadArticleReportStatusException.class);
        }
    }

    @DisplayName("rejectReport 은")
    @Nested
    class Context_rejectReport {

        @DisplayName("성공적으로 REPORT_REJECT 상태로 전환할 수 있다.")
        @Test
        void changeArticleStatus_toWaitForReview_willSuccess() {

            // given
            LocalDateTime reportTimeStamp = LocalDateTime.now();
            Article article = Article.builder()
                .id(1L)
                .member(createSimpleMember(1L))
                .title("title")
                .content("content")
                .anonymity(true)
                .reportStatus(ReportStatus.WAIT_FOR_REPORT_REVIEW)
                .build();

            given(articleRepository.findById(anyLong())).willReturn(Optional.of(article));

            // when
            articleReportService.rejectReport(
                new ArticleReportServiceRequest(article.getId(), reportTimeStamp));

            // then
            Assertions.assertThat(article.getReportStatus())
                .isEqualTo(ReportStatus.REPORT_REJECT);
        }

        @DisplayName("만약 이미 WAIT_FOR_REPORT_REVIEW 상태가 아니라면 실패한다.")
        @EnumSource(value = ReportStatus.class, names = {"REPORT_ACCEPT", "REPORT_REJECT",
            "NORMAL"})
        @ParameterizedTest
        void reportStatus_notWaitForReportReview_willFail(ReportStatus reportStatus) {

            // given
            LocalDateTime reportTimeStamp = LocalDateTime.now();
            Article article = Article.builder()
                .id(1L)
                .member(createSimpleMember(1L))
                .title("title")
                .content("content")
                .anonymity(true)
                .reportStatus(reportStatus)
                .build();

            given(articleRepository.findById(anyLong())).willReturn(Optional.of(article));

            // when then
            Assertions.assertThatThrownBy(() -> articleReportService.rejectReport(
                    new ArticleReportServiceRequest(article.getId(), reportTimeStamp)))
                .isInstanceOf(BadArticleReportStatusException.class);
        }
    }

    private static Member createSimpleMember(Long memberId) {
        return Member.builder().id(memberId).build();
    }
}
