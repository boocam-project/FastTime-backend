package com.fasttime.domain.report.unit.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.fasttime.domain.article.entity.Article;
import com.fasttime.domain.article.entity.ReportStatus;
import com.fasttime.domain.article.exception.ArticleDeletedException;
import com.fasttime.domain.article.exception.ArticleNotFoundException;
import com.fasttime.domain.article.repository.ArticleRepository;
import com.fasttime.domain.member.entity.Member;
import com.fasttime.domain.member.exception.MemberNotFoundException;
import com.fasttime.domain.member.service.MemberService;
import com.fasttime.domain.report.dto.request.CreateReportRequestDTO;
import com.fasttime.domain.report.entity.Report;
import com.fasttime.domain.report.exception.DuplicateReportException;
import com.fasttime.domain.report.repository.ReportRepository;
import com.fasttime.domain.report.service.ReportService;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@Transactional
@ExtendWith(MockitoExtension.class)
public class ReportServiceTest {

    @InjectMocks
    private ReportService reportService;

    @Mock
    private ReportRepository reportRepository;

    @Mock
    private ArticleRepository articleRepository;

    @Mock
    private MemberService memberService;

    @Nested
    @DisplayName("createReport()는 ")
    class Context_createReport {

        @Test
        @DisplayName("게시글을 신고할 수 있다.")
        void _willSuccess() {
            // given
            CreateReportRequestDTO request = CreateReportRequestDTO.builder().articleId(1L)
                .build();
            Optional<Article> article = Optional.of(Article.builder().id(1L).build());
            Member member = Member.builder().id(1L).build();
            Report report = Report.builder().id(1L).member(member).article(article.get()).build();
            Optional<List<Report>> reports = Optional.of(new ArrayList<>());
            given(articleRepository.findById(any(Long.class))).willReturn(article);
            given(memberService.getMember(any(Long.class))).willReturn(member);
            given(reportRepository.findAllByArticle(any(Article.class))).willReturn(reports);
            given(reportRepository.save(any(Report.class))).willReturn(report);

            // when
            reportService.createReport(request, 1L);

            // then
            verify(articleRepository, times(1)).findById(any(Long.class));
            verify(memberService, times(1)).getMember(any(Long.class));
            verify(reportRepository, times(1)).findAllByArticle(any(Article.class));
            verify(reportRepository, times(1)).save(any(Report.class));
        }

        @Test
        @DisplayName("게시글 10번째 신고 시 게시글 상태를 수정할 수 있다.")
        void article_reported_willSuccess() {
            // given
            CreateReportRequestDTO request = CreateReportRequestDTO.builder().articleId(1L)
                .build();
            Optional<Article> article = Optional.of(Article.builder().id(1L).reportStatus(ReportStatus.NORMAL).build());
            Member member = Member.builder().id(1L).build();
            Optional<List<Report>> reports = Optional.of(new ArrayList<>());
            for (long i = 1L; i < 10L; i++) {
                reports.get().add(Report.builder().id(i).build());
            }

            given(articleRepository.findById(any(Long.class))).willReturn(article);
            given(memberService.getMember(any(Long.class))).willReturn(member);
            given(reportRepository.findAllByArticle(any(Article.class))).willReturn(reports);

            // when
            reportService.createReport(request, 11L);

            // then
            verify(articleRepository, times(1)).findById(any(Long.class));
            verify(memberService, times(1)).getMember(any(Long.class));
            verify(reportRepository, times(1)).findAllByArticle(any(Article.class));
            verify(reportRepository, times(1)).save(any(Report.class));
        }

        @Test
        @DisplayName("게시글 20번째 신고 시 게시글을 삭제할 수 있다.")
        void article_delete_willSuccess() {
            // given
            CreateReportRequestDTO request = CreateReportRequestDTO.builder().articleId(0L)
                .build();
            Optional<Article> article = Optional.of(Article.builder().id(0L).reportStatus(ReportStatus.WAIT_FOR_REPORT_REVIEW).build());
            Member member = Member.builder().id(0L).build();
            Optional<List<Report>> reports = Optional.of(new ArrayList<>());
            for (long i = 1L; i < 20L; i++) {
                reports.get().add(Report.builder().id(i).build());
            }
            given(articleRepository.findById(any(Long.class))).willReturn(article);
            given(memberService.getMember(any(Long.class))).willReturn(member);
            given(reportRepository.findAllByArticle(any(Article.class))).willReturn(reports);

            // when
            reportService.createReport(request, 21L);

            // then
            verify(articleRepository, times(1)).findById(any(Long.class));
            verify(memberService, times(1)).getMember(any(Long.class));
            verify(reportRepository, times(1)).findAllByArticle(any(Article.class));
            verify(reportRepository, times(1)).save(any(Report.class));
        }

        @Test
        @DisplayName("게시물을 찾을 수 없으면 신고할 수 없다.")
        void articleNotFound_willFail() {
            // given
            CreateReportRequestDTO request = CreateReportRequestDTO.builder().articleId(1L)
                .build();
            Optional<Article> article = Optional.empty();
            given(articleRepository.findById(any(Long.class))).willReturn(article);

            // when, then
            Throwable exception = assertThrows(ArticleNotFoundException.class, () -> {
                reportService.createReport(request, 1L);
            });
            assertEquals("존재하지 않는 게시글입니다.", exception.getMessage());
            verify(articleRepository, times(1)).findById(any(Long.class));
            verify(memberService, never()).getMember(any(Long.class));
            verify(reportRepository, never()).findAllByArticle(any(Article.class));
            verify(reportRepository, never()).save(any(Report.class));
        }

        @Test
        @DisplayName("이미 삭제된 게시물은 신고할 수 없다.")
        void alreadyDeletedPost_willFail() {
            // given
            CreateReportRequestDTO request = CreateReportRequestDTO.builder().articleId(1L)
                .build();
            Optional<Article> article = Optional.of(
                Article.builder().id(1L).reportStatus(ReportStatus.WAIT_FOR_REPORT_REVIEW).build());
            article.get().approveReport(LocalDateTime.now());
            given(articleRepository.findById(any(Long.class))).willReturn(article);

            // when, then
            Throwable exception = assertThrows(ArticleDeletedException.class, () -> {
                reportService.createReport(request, 1L);
            });
            assertEquals("존재하지 않는 게시글입니다.", exception.getMessage());

            verify(articleRepository, times(1)).findById(any(Long.class));
            verify(memberService, never()).getMember(any(Long.class));
            verify(reportRepository, never()).findAllByArticle(any(Article.class));
            verify(reportRepository, never()).save(any(Report.class));
        }

        @Test
        @DisplayName("회원을 찾을 수 없으면 신고할 수 없다.")
        void memberNotFound_willFail() {
            // given
            CreateReportRequestDTO request = CreateReportRequestDTO.builder().articleId(1L)
                .build();
            Optional<Article> article = Optional.of(Article.builder().id(1L).build());

            given(articleRepository.findById(any(Long.class))).willReturn(article);
            given(memberService.getMember(any(Long.class))).willThrow(
                new MemberNotFoundException("User not found with id: 1L"));

            // when, then
            Throwable exception = assertThrows(MemberNotFoundException.class, () -> {
                reportService.createReport(request, 1L);
            });
            assertEquals("User not found with id: 1L", exception.getMessage());

            verify(articleRepository, times(1)).findById(any(Long.class));
            verify(memberService, times(1)).getMember(any(Long.class));
            verify(reportRepository, never()).findAllByArticle(any(Article.class));
            verify(reportRepository, never()).save(any(Report.class));
        }

        @Test
        @DisplayName("중복 신고할 수 없다.")
        void duplicate_report_willFail() {
            // given
            CreateReportRequestDTO request = CreateReportRequestDTO.builder().articleId(0L)
                .build();
            Optional<Article> article = Optional.of(Article.builder().id(0L).build());
            Member member = Member.builder().id(0L).build();
            Optional<List<Report>> reports = Optional.of(new ArrayList<>());
            reports.get().add(Report.builder().article(article.get()).member(member).build());

            given(articleRepository.findById(any(Long.class))).willReturn(article);
            given(memberService.getMember(any(Long.class))).willReturn(member);
            given(reportRepository.findAllByArticle(any(Article.class))).willReturn(reports);

            // when, then
            Throwable exception = assertThrows(DuplicateReportException.class, () -> {
                reportService.createReport(request, 1L);
            });
            assertEquals("이미 신고한 게시글입니다.", exception.getMessage());

            verify(articleRepository, times(1)).findById(any(Long.class));
            verify(memberService, times(1)).getMember(any(Long.class));
            verify(reportRepository, times(1)).findAllByArticle(any(Article.class));
            verify(reportRepository, never()).save(any(Report.class));
        }
    }
}
