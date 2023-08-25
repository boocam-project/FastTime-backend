package com.fasttime.domain.report.service;

import com.fasttime.domain.member.entity.Member;
import com.fasttime.domain.member.service.MemberService;
import com.fasttime.domain.post.entity.Post;
import com.fasttime.domain.post.exception.PostNotFoundException;
import com.fasttime.domain.post.repository.PostRepository;
import com.fasttime.domain.report.dto.ReportDTO;
import com.fasttime.domain.report.dto.request.CreateReportRequest;
import com.fasttime.domain.report.entity.Report;
import com.fasttime.domain.report.exception.AlreadyDeletedPostException;
import com.fasttime.domain.report.exception.DuplicateReportException;
import com.fasttime.domain.report.repository.ReportRepository;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
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

    public ReportDTO createReport(CreateReportRequest req) {
        // TODO post, member 각각 postService, memberService 를 통해 읽어 온다.
        Optional<Post> post = postRepository.findById(req.getPostId());
        checkPost(post);
        Member member = memberService.getMember(req.getMemberId());
        List<Report> reports = getReportsByPost(post.get());
        checkDuplicateReports(reports, member);
        checkReports(reports, post);
        return reportRepository.save(
            Report.builder().member(member).post(post.get()).build()).toDTO();

    }

    // 게시글에 대한 신고 내역 조회 메서드
    public List<Report> getReportsByPost(Post post) {
        Optional<List<Report>> reports = reportRepository.findAllByPost(post);
        if (reports.isEmpty()) {
            return new ArrayList<>();
        } else {
            return reports.get();
        }
    }

    // 게시글 존재 여부 & 이미 삭제된 게시물 체크
    private void checkPost(Optional<Post> post) {
        if (post.isEmpty()) {
            throw new PostNotFoundException();
        } else if (post.get().isDeleted()) {
            throw new AlreadyDeletedPostException("이미 삭제된 게시글입니다.");
        }
    }

    // 중복 신고 체크
    private void checkDuplicateReports(List<Report> reports, Member member) {
        for (Report report : reports) {
            if (report.getMember() == member) {
                throw new DuplicateReportException("이미 신고한 게시글입니다.");
            }
        }

    }

    // 신고 누적 횟수 체크 및 그에 따른 게시글 신고 상태 변경 혹은 삭제
    // 신고 누적 10: NORMAL 에서 REPORTED 로 신고 상태 변경
    // 신고 누적 20: 삭제
    private void checkReports(List<Report> reports, Optional<Post> post) {
        if (reports.size() == 9) {
            post.get().report();
            Optional<Post> reportedPost = postRepository.findById(post.get().getId());
            if (reportedPost.isPresent()) {
                log.info("10번 신고된 게시글 신고 상태 변경: " + reportedPost.get().getReportStatus());
            }
        } else if (reports.size() == 19) {
            post.get().approveReport(LocalDateTime.now());
            Optional<Post> reportedPost = postRepository.findById(post.get().getId());
            if (reportedPost.isPresent()) {
                log.info("20번 신고된 게시글 삭제: " + reportedPost.get().getDeletedAt());
            }
        }
    }
}
