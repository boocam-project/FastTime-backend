package com.fasttime.domain.report.service;

import com.fasttime.domain.article.entity.Article;
import com.fasttime.domain.article.exception.ArticleDeletedException;
import com.fasttime.domain.article.exception.ArticleNotFoundException;
import com.fasttime.domain.article.repository.ArticleRepository;
import com.fasttime.domain.member.entity.Member;
import com.fasttime.domain.member.service.MemberService;
import com.fasttime.domain.report.dto.request.CreateReportRequestDTO;
import com.fasttime.domain.report.entity.Report;
import com.fasttime.domain.report.exception.DuplicateReportException;
import com.fasttime.domain.report.repository.ReportRepository;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class ReportService {

    private final ReportRepository reportRepository;
    private final ArticleRepository articleRepository;
    private final MemberService memberService;

    public void createReport(CreateReportRequestDTO createReportRequestDTO, Long memberId) {
        Article article = articleRepository.findById(createReportRequestDTO.getArticleId())
            .orElseThrow(ArticleNotFoundException::new);
        checkPostAlreadyDeleted(article);
        Member member = memberService.getMember(memberId);
        List<Report> reports = reportRepository.findAllByPost(article).orElseGet(ArrayList::new);
        checkDuplicateReports(reports, member);
        checkHowManyReportsOfPost(reports, article);
        reportRepository.save(Report.builder().member(member).article(article).build());
    }

    private void checkPostAlreadyDeleted(Article article) {
        if (article.isDeleted()) {
            throw new ArticleDeletedException();
        }
    }

    private void checkDuplicateReports(List<Report> reports, Member member) {
        for (Report report : reports) {
            if (report.getMember() == member) {
                throw new DuplicateReportException();
            }
        }
    }

    private void checkHowManyReportsOfPost(List<Report> reports, Article article) {
        if (is10thReport(reports)) {
            article.transToWaitForReview();
        } else if (is20thReport(reports)) {
            article.approveReport(LocalDateTime.now());
        }
    }

    private boolean is10thReport(List<Report> reports) {
        return reports.size() + 1 == 10;
    }

    private boolean is20thReport(List<Report> reports) {
        return reports.size() + 1 == 20;
    }
}
