package com.fasttime.domain.report.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.fasttime.domain.comment.exception.NotFoundException;
import com.fasttime.domain.member.entity.Member;
import com.fasttime.domain.member.repository.MemberRepository;
import com.fasttime.domain.post.entity.Post;
import com.fasttime.domain.post.entity.ReportStatus;
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
    private PostRepository postRepository;

    @Mock
    private MemberRepository memberRepository;

    @Nested
    @DisplayName("createReport()는 ")
    class Context_createReport {

        @Test
        @DisplayName("게시글을 신고할 수 있다.")
        void _willSuccess() {
            // given
            CreateReportRequest request = CreateReportRequest.builder().postId(0L).memberId(0L)
                .build();
            Optional<Post> post = Optional.of(Post.builder().id(0L).build());
            Optional<Member> member = Optional.of(Member.builder().id(0L).build());
            Report report = Report.builder().id(0L).member(member.get()).post(post.get()).build();

            given(postRepository.findById(any(Long.class))).willReturn(post);
            given(memberRepository.findById(any(Long.class))).willReturn(member);
            given(reportRepository.save(any(Report.class))).willReturn(report);

            // when
            ReportDTO reportDto = reportService.createReport(request);

            // then
            assertThat(reportDto).extracting("id", "postId", "memberId")
                .containsExactly(0L, 0L, 0L);
            verify(postRepository, times(1)).findById(any(Long.class));
            verify(memberRepository, times(1)).findById(any(Long.class));
            verify(reportRepository, times(1)).save(any(Report.class));
        }

        @Test
        @DisplayName("게시글 10번째 신고 시 게시글 상태를 수정할 수 있다.")
        void post_reported_willSuccess() {
            // given
            CreateReportRequest request = CreateReportRequest.builder().postId(0L).memberId(0L)
                .build();
            Optional<Post> post = Optional.of(Post.builder().id(0L).build());
            Optional<Member> member = Optional.of(Member.builder().id(0L).build());
            Report report = Report.builder().id(0L).member(member.get()).post(post.get()).build();
            List<Report> reportList = new ArrayList<>();
            Optional<List<Report>> reports = Optional.of(reportList);
            for (long i = 1L; i < 10L; i++) {
                reports.get().add(Report.builder().id(i).build());
            }

            given(postRepository.findById(any(Long.class))).willReturn(post);
            given(memberRepository.findById(any(Long.class))).willReturn(member);
            given(reportRepository.save(any(Report.class))).willReturn(report);
            given(reportRepository.findAllByPost(any(Post.class))).willReturn(reports);

            // when
            ReportDTO reportDto = reportService.createReport(request);

            // then
            assertThat(reportDto).extracting("id", "postId", "memberId")
                .containsExactly(0L, 0L, 0L);
            verify(postRepository, times(2)).findById(any(Long.class));
            verify(memberRepository, times(1)).findById(any(Long.class));
            verify(reportRepository, times(1)).findAllByPost(any(Post.class));
            verify(reportRepository, times(1)).save(any(Report.class));
        }

        @Test
        @DisplayName("게시글 20번째 신고 시 게시글을 삭제할 수 있다.")
        void post_delete_willSuccess() {
            // given
            CreateReportRequest request = CreateReportRequest.builder().postId(0L).memberId(0L)
                .build();
            Optional<Post> post = Optional.of(Post.builder().id(0L).build());
            Optional<Member> member = Optional.of(Member.builder().id(0L).build());
            Report report = Report.builder().id(0L).member(member.get()).post(post.get()).build();
            List<Report> reportList = new ArrayList<>();
            Optional<List<Report>> reports = Optional.of(reportList);
            for (long i = 1L; i < 10L; i++) {
                reports.get().add(Report.builder().id(i).build());
            }
            given(postRepository.findById(any(Long.class))).willReturn(post);
            given(memberRepository.findById(any(Long.class))).willReturn(member);
            given(reportRepository.save(any(Report.class))).willReturn(report);
            given(reportRepository.findAllByPost(any(Post.class))).willReturn(reports);

            // when
            ReportDTO reportDto = reportService.createReport(request);

            // then
            assertThat(reportDto).extracting("id", "postId", "memberId")
                .containsExactly(0L, 0L, 0L);
            verify(postRepository, times(2)).findById(any(Long.class));
            verify(memberRepository, times(1)).findById(any(Long.class));
            verify(reportRepository, times(1)).findAllByPost(any(Post.class));
            verify(reportRepository, times(1)).save(any(Report.class));
        }

        @Test
        @DisplayName("게시물을 찾을 수 없으면 신고할 수 없다.")
        void postNotFound_willFail() {
            // given
            CreateReportRequest request = CreateReportRequest.builder().postId(0L).memberId(0L)
                .build();
            Optional<Post> post = Optional.empty();

            given(postRepository.findById(any(Long.class))).willReturn(post);

            // when, then
            Throwable exception = assertThrows(NotFoundException.class, () -> {
                reportService.createReport(request);
            });
            assertEquals("존재하지 않는 게시글입니다.", exception.getMessage());

            verify(postRepository, times(1)).findById(any(Long.class));
            verify(reportRepository, never()).findAllByPost(any(Post.class));
            verify(reportRepository, never()).save(any(Report.class));
        }

        @Test
        @DisplayName("이미 삭제된 게시물은 신고할 수 없다.")
        void alreadyDeletedPost_willFail() {
            // given
            CreateReportRequest request = CreateReportRequest.builder().postId(0L).memberId(0L)
                .build();
            Optional<Post> post = Optional.of(
                Post.builder().id(0L).reportStatus(ReportStatus.REPORTED).build());
            post.get().approveReport(LocalDateTime.now());
            given(postRepository.findById(any(Long.class))).willReturn(post);

            // when, then
            Throwable exception = assertThrows(AlreadyDeletedPostException.class, () -> {
                reportService.createReport(request);
            });
            assertEquals("이미 삭제된 게시글입니다.", exception.getMessage());

            verify(postRepository, times(1)).findById(any(Long.class));
            verify(reportRepository, never()).findAllByPost(any(Post.class));
            verify(reportRepository, never()).save(any(Report.class));
        }

        @Test
        @DisplayName("회원을 찾을 수 없으면 신고할 수 없다.")
        void memberNotFound_willFail() {
            // given
            CreateReportRequest request = CreateReportRequest.builder().postId(0L).memberId(0L)
                .build();
            Optional<Post> post = Optional.of(Post.builder().id(0L).build());
            Optional<Member> member = Optional.empty();

            given(postRepository.findById(any(Long.class))).willReturn(post);
            given(memberRepository.findById(any(Long.class))).willReturn(member);

            // when, then
            Throwable exception = assertThrows(NotFoundException.class, () -> {
                reportService.createReport(request);
            });
            assertEquals("존재하지 않는 회원입니다.", exception.getMessage());

            verify(postRepository, times(1)).findById(any(Long.class));
            verify(memberRepository, times(1)).findById(any(Long.class));
            verify(reportRepository, never()).findAllByPost(any(Post.class));
            verify(reportRepository, never()).save(any(Report.class));
        }

        @Test
        @DisplayName("중복 신고할 수 없다.")
        void duplicate_report_willFail() {
            // given
            CreateReportRequest request = CreateReportRequest.builder().postId(0L).memberId(0L)
                .build();
            Optional<Post> post = Optional.of(Post.builder().id(0L).build());
            Optional<Member> member = Optional.of(Member.builder().id(0L).build());
            List<Report> reportList = new ArrayList<>();
            reportList.add(Report.builder().post(post.get()).member(member.get()).build());
            Optional<List<Report>> reports = Optional.of(reportList);

            given(postRepository.findById(any(Long.class))).willReturn(post);
            given(memberRepository.findById(any(Long.class))).willReturn(member);
            given(reportRepository.findAllByPost(any(Post.class))).willReturn(reports);

            // when, then
            Throwable exception = assertThrows(DuplicateReportException.class, () -> {
                reportService.createReport(request);
            });
            assertEquals("이미 신고한 게시글입니다.", exception.getMessage());

            verify(postRepository, times(1)).findById(any(Long.class));
            verify(memberRepository, times(1)).findById(any(Long.class));
            verify(reportRepository, times(1)).findAllByPost(any(Post.class));
            verify(reportRepository, never()).save(any(Report.class));
        }
    }

    @Nested
    @DisplayName("getReportsByPost()는 ")
    class Context_getReportsByPost {

        @Test
        @DisplayName("주어진 게시글의 신고 내역을 가져올 수 있다.")
        void _willSuccess() {
            // given
            Post post = Post.builder().id(0L).build();
            Member member = Member.builder().id(0L).build();
            List<Report> reportList = new ArrayList<>();
            reportList.add(Report.builder().post(post).member(member).build());
            Optional<List<Report>> reports = Optional.of(reportList);

            given(reportRepository.findAllByPost(any(Post.class))).willReturn(reports);

            // when
            Optional<List<Report>> result = reportService.getReportsByPost(post);

            // then
            assertThat(result).isEqualTo(reports);

            verify(reportRepository, times(1)).findAllByPost(any(Post.class));
        }
    }
}
