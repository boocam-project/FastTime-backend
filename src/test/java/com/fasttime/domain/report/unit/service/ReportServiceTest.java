package com.fasttime.domain.report.unit.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.fasttime.domain.member.entity.Member;
import com.fasttime.domain.member.exception.UserNotFoundException;
import com.fasttime.domain.member.service.MemberService;
import com.fasttime.domain.post.entity.Post;
import com.fasttime.domain.post.entity.ReportStatus;
import com.fasttime.domain.post.exception.PostDeletedException;
import com.fasttime.domain.post.exception.PostNotFoundException;
import com.fasttime.domain.post.repository.PostRepository;
import com.fasttime.domain.report.dto.request.CreateReportRequestDTO;
import com.fasttime.domain.report.entity.Report;
import com.fasttime.domain.report.exception.DuplicateReportException;
import com.fasttime.domain.report.repository.ReportRepository;
import com.fasttime.domain.report.service.ReportService;
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
    private MemberService memberService;

    @Nested
    @DisplayName("createReport()는 ")
    class Context_createReport {

        @Test
        @DisplayName("게시글을 신고할 수 있다.")
        void _willSuccess() {
            // given
            CreateReportRequestDTO request = CreateReportRequestDTO.builder().postId(1L)
                .build();
            Optional<Post> post = Optional.of(Post.builder().id(1L).build());
            Member member = Member.builder().id(1L).build();
            Report report = Report.builder().id(1L).member(member).post(post.get()).build();
            Optional<List<Report>> reports = Optional.of(new ArrayList<>());
            given(postRepository.findById(any(Long.class))).willReturn(post);
            given(memberService.getMember(any(Long.class))).willReturn(member);
            given(reportRepository.findAllByPost(any(Post.class))).willReturn(reports);
            given(reportRepository.save(any(Report.class))).willReturn(report);

            // when
            reportService.createReport(request, 1L);

            // then
            verify(postRepository, times(1)).findById(any(Long.class));
            verify(memberService, times(1)).getMember(any(Long.class));
            verify(reportRepository, times(1)).findAllByPost(any(Post.class));
            verify(reportRepository, times(1)).save(any(Report.class));
        }

        @Test
        @DisplayName("게시글 10번째 신고 시 게시글 상태를 수정할 수 있다.")
        void post_reported_willSuccess() {
            // given
            CreateReportRequestDTO request = CreateReportRequestDTO.builder().postId(1L)
                .build();
            Optional<Post> post = Optional.of(Post.builder().id(1L).build());
            Member member = Member.builder().id(1L).build();
            Optional<List<Report>> reports = Optional.of(new ArrayList<>());
            for (long i = 1L; i < 10L; i++) {
                reports.get().add(Report.builder().id(i).build());
            }

            given(postRepository.findById(any(Long.class))).willReturn(post);
            given(memberService.getMember(any(Long.class))).willReturn(member);
            given(reportRepository.findAllByPost(any(Post.class))).willReturn(reports);

            // when
            reportService.createReport(request, 11L);

            // then
            verify(postRepository, times(1)).findById(any(Long.class));
            verify(memberService, times(1)).getMember(any(Long.class));
            verify(reportRepository, times(1)).findAllByPost(any(Post.class));
            verify(reportRepository, times(1)).save(any(Report.class));
        }

        @Test
        @DisplayName("게시글 20번째 신고 시 게시글을 삭제할 수 있다.")
        void post_delete_willSuccess() {
            // given
            CreateReportRequestDTO request = CreateReportRequestDTO.builder().postId(0L)
                .build();
            Optional<Post> post = Optional.of(Post.builder().id(0L).build());
            Member member = Member.builder().id(0L).build();
            Optional<List<Report>> reports = Optional.of(new ArrayList<>());
            for (long i = 1L; i < 20L; i++) {
                reports.get().add(Report.builder().id(i).build());
            }
            given(postRepository.findById(any(Long.class))).willReturn(post);
            given(memberService.getMember(any(Long.class))).willReturn(member);
            given(reportRepository.findAllByPost(any(Post.class))).willReturn(reports);

            // when
            reportService.createReport(request, 21L);

            // then
            verify(postRepository, times(1)).findById(any(Long.class));
            verify(memberService, times(1)).getMember(any(Long.class));
            verify(reportRepository, times(1)).findAllByPost(any(Post.class));
            verify(reportRepository, times(1)).save(any(Report.class));
        }

        @Test
        @DisplayName("게시물을 찾을 수 없으면 신고할 수 없다.")
        void postNotFound_willFail() {
            // given
            CreateReportRequestDTO request = CreateReportRequestDTO.builder().postId(1L)
                .build();
            Optional<Post> post = Optional.empty();
            given(postRepository.findById(any(Long.class))).willReturn(post);

            // when, then
            Throwable exception = assertThrows(PostNotFoundException.class, () -> {
                reportService.createReport(request, 1L);
            });
            assertEquals("존재하지 않는 게시글입니다.", exception.getMessage());
            verify(postRepository, times(1)).findById(any(Long.class));
            verify(memberService, never()).getMember(any(Long.class));
            verify(reportRepository, never()).findAllByPost(any(Post.class));
            verify(reportRepository, never()).save(any(Report.class));
        }

        @Test
        @DisplayName("이미 삭제된 게시물은 신고할 수 없다.")
        void alreadyDeletedPost_willFail() {
            // given
            CreateReportRequestDTO request = CreateReportRequestDTO.builder().postId(1L)
                .build();
            Optional<Post> post = Optional.of(
                Post.builder().id(1L).reportStatus(ReportStatus.REPORTED).build());
            post.get().approveReport(LocalDateTime.now());
            given(postRepository.findById(any(Long.class))).willReturn(post);

            // when, then
            Throwable exception = assertThrows(PostDeletedException.class, () -> {
                reportService.createReport(request, 1L);
            });
            assertEquals("존재하지 않는 게시글입니다.", exception.getMessage());

            verify(postRepository, times(1)).findById(any(Long.class));
            verify(memberService, never()).getMember(any(Long.class));
            verify(reportRepository, never()).findAllByPost(any(Post.class));
            verify(reportRepository, never()).save(any(Report.class));
        }

        @Test
        @DisplayName("회원을 찾을 수 없으면 신고할 수 없다.")
        void memberNotFound_willFail() {
            // given
            CreateReportRequestDTO request = CreateReportRequestDTO.builder().postId(1L)
                .build();
            Optional<Post> post = Optional.of(Post.builder().id(1L).build());

            given(postRepository.findById(any(Long.class))).willReturn(post);
            given(memberService.getMember(any(Long.class))).willThrow(
                new UserNotFoundException("User not found with id: 1L"));

            // when, then
            Throwable exception = assertThrows(UserNotFoundException.class, () -> {
                reportService.createReport(request, 1L);
            });
            assertEquals("User not found with id: 1L", exception.getMessage());

            verify(postRepository, times(1)).findById(any(Long.class));
            verify(memberService, times(1)).getMember(any(Long.class));
            verify(reportRepository, never()).findAllByPost(any(Post.class));
            verify(reportRepository, never()).save(any(Report.class));
        }

        @Test
        @DisplayName("중복 신고할 수 없다.")
        void duplicate_report_willFail() {
            // given
            CreateReportRequestDTO request = CreateReportRequestDTO.builder().postId(0L)
                .build();
            Optional<Post> post = Optional.of(Post.builder().id(0L).build());
            Member member = Member.builder().id(0L).build();
            Optional<List<Report>> reports = Optional.of(new ArrayList<>());
            reports.get().add(Report.builder().post(post.get()).member(member).build());

            given(postRepository.findById(any(Long.class))).willReturn(post);
            given(memberService.getMember(any(Long.class))).willReturn(member);
            given(reportRepository.findAllByPost(any(Post.class))).willReturn(reports);

            // when, then
            Throwable exception = assertThrows(DuplicateReportException.class, () -> {
                reportService.createReport(request, 1L);
            });
            assertEquals("이미 신고한 게시글입니다.", exception.getMessage());

            verify(postRepository, times(1)).findById(any(Long.class));
            verify(memberService, times(1)).getMember(any(Long.class));
            verify(reportRepository, times(1)).findAllByPost(any(Post.class));
            verify(reportRepository, never()).save(any(Report.class));
        }
    }
}
