package com.fasttime.domain.post.unit.entity;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasttime.domain.post.entity.Post;
import com.fasttime.domain.post.entity.ReportStatus;
import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

class PostTest {

    @DisplayName("게시글을 생성할 수 있다.")
    @Test
    void post_create_willSuccess(){
        // given
        String title = "제목1";
        String content = "내용1";
        boolean anounumity = true;

        // when
        Post createdPost = Post.createNewPost(null, title, content, anounumity);

        // then
        assertThat(createdPost).extracting("title", "content", "anonymity")
            .containsExactly(title, content, anounumity);
    }

    @DisplayName("게시글의 내용을 변경할 수 있다.")
    @Test
    void post_update_willSuccess(){
        // given
        String title = "제목1";
        String content = "내용1";
        boolean anounumity = true;
        Post createdPost = Post.createNewPost(null, title, content, anounumity);

        // when
        String updateContent = "새로운 내용1";
        createdPost.update(updateContent);

        // then
        assertThat(createdPost).extracting("title", "content", "anonymity")
            .containsExactly(title, updateContent, anounumity);
    }


    @DisplayName("report()는")
    @Nested
    class Context_report{

        @DisplayName("ReportStatus를 REPORTED로 변경할 수 있다.")
        @Test
        void reportStatus_isNotReported_willChangedToReportPending(){
            // given
            Post testPost = Post.builder()
                .reportStatus(ReportStatus.NORMAL)
                .build();

            // when
            testPost.report();

            // then
            assertThat(testPost.getReportStatus()).isSameAs(ReportStatus.REPORTED);
        }

        @DisplayName("이미 신고당한 상태면 ReportStatus를 변경할 수 없다.")
        @EnumSource(value = ReportStatus.class, names = {"REPORTED", "REPORT_REJECTED"})
        @ParameterizedTest
        void reportStatus_alreadyReported_willNotChanged(ReportStatus reportStatus){
            // given
            Post testPost = Post.builder()
                .reportStatus(reportStatus)
                .build();

            // when
            testPost.report();

            // then
            assertThat(testPost.getReportStatus()).isSameAs(reportStatus);
        }
    }

    @DisplayName("approveReport()는")
    @Nested
    class Context_approveReport{

        @DisplayName("Post를 삭제할 수 있다.")
        @Test
        void approved_report_willDeleted(){
            // given
            LocalDateTime deletedTime = LocalDateTime.now();
            Post testPost = Post.builder()
                .reportStatus(ReportStatus.REPORTED)
                .build();

            // when
            testPost.approveReport(deletedTime);

            // then
            assertThat(testPost.getDeletedAt()).isEqualTo(deletedTime);
            assertThat(testPost.isDeleted()).isTrue();
        }

        @DisplayName("ReportStatus가 Reported 가 아니면 approve 할 수 없다.")
        @EnumSource(value = ReportStatus.class, names = {"NORMAL", "REPORT_REJECTED"})
        @ParameterizedTest
        void post_isNotReported_willNotChanged(ReportStatus reportStatus){
            // given
            LocalDateTime deletedTime = LocalDateTime.now();
            Post testPost = Post.builder()
                .reportStatus(reportStatus)
                .build();

            // when
            testPost.approveReport(deletedTime);

            // then
            assertThat(testPost.getDeletedAt()).isNull();
            assertThat(testPost.isDeleted()).isFalse();
        }
    }

    @DisplayName("rejectReport()는")
    @Nested
    class Context_rejectReport{

        @DisplayName("ReportStatus를 REPORT_REJECTED로 변경할 수 있다.")
        @Test
        void reportStatus_isReported_willChangedToReportRejected(){
            // given
            Post testPost = Post.builder()
                .reportStatus(ReportStatus.REPORTED)
                .build();

            // when
            testPost.rejectReport();

            // then
            assertThat(testPost.getReportStatus()).isSameAs(ReportStatus.REPORT_REJECTED);
        }

        @DisplayName("일반적인 게시글은 Reject할 수 없다 변경할 수 없다.")
        @EnumSource(value = ReportStatus.class, names = {"NORMAL", "REPORT_REJECTED"})
        @ParameterizedTest
        void post_isNotReported_willNotChanged(ReportStatus reportStatus){
            // given
            Post testPost = Post.builder()
                .reportStatus(reportStatus)
                .build();

            // when
            testPost.rejectReport();

            // then
            assertThat(testPost.getReportStatus()).isSameAs(reportStatus);
        }
    }

}
