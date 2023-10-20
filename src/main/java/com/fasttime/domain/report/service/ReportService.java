package com.fasttime.domain.report.service;

import com.fasttime.domain.member.entity.Member;
import com.fasttime.domain.member.service.MemberService;
import com.fasttime.domain.post.entity.Post;
import com.fasttime.domain.post.exception.PostNotFoundException;
import com.fasttime.domain.post.repository.PostRepository;
import com.fasttime.domain.report.dto.request.CreateReportRequestDTO;
import com.fasttime.domain.report.entity.Report;
import com.fasttime.domain.report.exception.AlreadyDeletedPostException;
import com.fasttime.domain.report.exception.DuplicateReportException;
import com.fasttime.domain.report.repository.ReportRepository;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import javax.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class ReportService {

    private final ReportRepository reportRepository;
    private final PostRepository postRepository;
    private final MemberService memberService;

    public void createReport(CreateReportRequestDTO createReportRequestDTO, Long memberId) {
        Post post = postRepository.findById(createReportRequestDTO.getPostId())
            .orElseThrow(PostNotFoundException::new);
        checkPostAlreadyDeleted(post);
        Member member = memberService.getMember(memberId);
        List<Report> reports = reportRepository.findAllByPost(post).orElseGet(ArrayList::new);
        checkDuplicateReports(reports, member);
        checkHowManyReportsOfPost(reports, post);
        reportRepository.save(Report.builder().member(member).post(post).build());
    }

    private void checkPostAlreadyDeleted(Post post) {
        if (post.isDeleted()) {
            throw new AlreadyDeletedPostException();
        }
    }

    private void checkDuplicateReports(List<Report> reports, Member member) {
        for (Report report : reports) {
            if (report.getMember() == member) {
                throw new DuplicateReportException();
            }
        }
    }

    private void checkHowManyReportsOfPost(List<Report> reports, Post post) {
        if (is10thReport(reports)) {
            post.report();
        } else if (is20thReport(reports)) {
            post.approveReport(LocalDateTime.now());
        }
    }

    private boolean is10thReport(List<Report> reports) {
        return reports.size() + 1 == 10;
    }

    private boolean is20thReport(List<Report> reports) {
        return reports.size() + 1 == 20;
    }
}
