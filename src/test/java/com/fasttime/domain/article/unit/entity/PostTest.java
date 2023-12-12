package com.fasttime.domain.article.unit.entity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.fasttime.domain.article.entity.Article;
import com.fasttime.domain.article.entity.ReportStatus;
import com.fasttime.domain.article.exception.ArticleDeletedException;
import com.fasttime.domain.article.exception.ArticleReportedException;
import com.fasttime.domain.article.exception.BadArticleReportStatusException;
import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

class ArticleTest {

    @DisplayName("게시글을 생성할 수 있다.")
    @Test
    void article_create_willSuccess(){
        // given
        String title = "제목1";
        String content = "내용1";
        boolean anonymity = true;

        // when
        Article createdArticle = Article.createNewArticle(null, title, content, anonymity);

        // then
        assertThat(createdArticle).extracting("title", "content", "anonymity")
            .containsExactly(title, content, anonymity);
    }

    @Nested
    class Context_update {

        @DisplayName("게시글의 내용을 변경할 수 있다.")
        @Test
        void article_update_willSuccess(){
            // given
            String title = "제목1";
            String content = "내용1";
            boolean anonymity = true;
            Article createdArticle = Article.createNewArticle(null, title, content, anonymity);

            // when
            String updateTitle = "새로운 제목1";
            String updateContent = "새로운 내용1";
            createdArticle.update(updateTitle, updateContent);

            // then
            assertThat(createdArticle).extracting("title", "content", "anonymity")
                .containsExactly(updateTitle, updateContent, anonymity);
        }

        @DisplayName("게시글이 검토중인 상태에서는 수정 할 수 없다.")
        @Test
        void article_isReported_willThrowArticleReportedException(){
            // given
            String title = "제목1";
            String content = "내용1";
            boolean anonymity = true;
            Article createdArticle = Article.createNewArticle(null, title, content, anonymity);
            createdArticle.transToWaitForReview();

            // when
            String updateTitle = "새로운 제목1";
            String updateContent = "새로운 내용1";
            assertThatThrownBy(() -> createdArticle.update(updateTitle, updateContent))
                .isInstanceOf(ArticleReportedException.class);
        }

        @DisplayName("삭제된 게시글 역시 수정 할 수 없다.")
        @Test
        void article_isDeleted_willThrowArticleDeletedException(){
            // given
            String title = "제목1";
            String content = "내용1";
            boolean anonymity = true;
            Article createdArticle = Article.createNewArticle(null, title, content, anonymity);
            createdArticle.delete(LocalDateTime.now());

            // when
            String updateTitle = "새로운 제목1";
            String updateContent = "새로운 내용1";
            assertThatThrownBy(() -> createdArticle.update(updateTitle, updateContent))
                .isInstanceOf(ArticleDeletedException.class);
        }
    }

    @DisplayName("report()는")
    @Nested
    class Context_report{

        @DisplayName("ReportStatus를 REPORTED로 변경할 수 있다.")
        @Test
        void reportStatus_isNotReported_willChangedToReportPending(){
            // given
            Article testArticle = Article.builder()
                .reportStatus(ReportStatus.NORMAL)
                .build();

            // when
            testArticle.transToWaitForReview();

            // then
            assertThat(testArticle.getReportStatus()).isSameAs(ReportStatus.WAIT_FOR_REPORT_REVIEW);
        }

        @DisplayName("이미 신고당한 상태면 ReportStatus를 변경할 수 없다.")
        @EnumSource(value = ReportStatus.class, names = {"WAIT_FOR_REPORT_REVIEW", "REPORT_REJECT"})
        @ParameterizedTest
        void reportStatus_alreadyReported_willNotChanged(ReportStatus reportStatus){
            // given
            Article testArticle = Article.builder()
                .reportStatus(reportStatus)
                .build();

            // when then
            assertThatThrownBy(testArticle::transToWaitForReview)
                .isInstanceOf(BadArticleReportStatusException.class);
        }
    }

    @DisplayName("approveReport()는")
    @Nested
    class Context_approveReport{

        @DisplayName("Article를 삭제할 수 있다.")
        @Test
        void approved_report_willDeleted(){
            // given
            LocalDateTime deletedTime = LocalDateTime.now();
            Article testArticle = Article.builder()
                .reportStatus(ReportStatus.WAIT_FOR_REPORT_REVIEW)
                .build();

            // when
            testArticle.approveReport(deletedTime);

            // then
            assertThat(testArticle.getDeletedAt()).isEqualTo(deletedTime);
            assertThat(testArticle.isDeleted()).isTrue();
        }

        @DisplayName("ReportStatus가 Reported 가 아니면 approve 할 수 없다.")
        @EnumSource(value = ReportStatus.class, names = {"NORMAL", "REPORT_REJECT"})
        @ParameterizedTest
        void article_isNotReported_willNotChanged(ReportStatus reportStatus){
            // given
            LocalDateTime deletedTime = LocalDateTime.now();
            Article testArticle = Article.builder()
                .reportStatus(reportStatus)
                .build();

            // when then
            assertThatThrownBy(() -> testArticle.approveReport(deletedTime))
                .isInstanceOf(BadArticleReportStatusException.class);
        }
    }

    @DisplayName("rejectReport()는")
    @Nested
    class Context_rejectReport{

        @DisplayName("ReportStatus를 REPORT_REJECTED로 변경할 수 있다.")
        @Test
        void reportStatus_isReported_willChangedToReportRejected(){
            // given
            Article testArticle = Article.builder()
                .reportStatus(ReportStatus.WAIT_FOR_REPORT_REVIEW)
                .build();

            // when
            testArticle.rejectReport();

            // then
            assertThat(testArticle.getReportStatus()).isSameAs(ReportStatus.REPORT_REJECT);
        }

        @DisplayName("일반적인 게시글은 Reject할 수 없다 변경할 수 없다.")
        @EnumSource(value = ReportStatus.class, names = {"NORMAL", "REPORT_REJECT"})
        @ParameterizedTest
        void article_isNotReported_willNotChanged(ReportStatus reportStatus){
            // given
            Article testArticle = Article.builder()
                .reportStatus(reportStatus)
                .build();

            // when then
            assertThatThrownBy(testArticle::rejectReport)
                .isInstanceOf(BadArticleReportStatusException.class);
        }
    }

    @DisplayName("delete()는")
    @Nested
    class Context_delete{

        @DisplayName("게시글을 삭제할 수 있다.")
        @Test
        void article_deletedAt_willSetNow(){
            // given
            LocalDateTime deletedAt = LocalDateTime.now();
            Article testArticle = Article.builder()
                .build();

            // when
            testArticle.delete(deletedAt);

            // then
            assertThat(testArticle.getDeletedAt()).isEqualTo(deletedAt);
            assertThat(testArticle.isDeleted()).isTrue();
        }

        @DisplayName("이미 삭제된 게시글은 삭제 시간을 갱신할 수 없다.")
        @Test
        void alreadyDeletedArticle_deletedAt_willNotSettingNow(){
            // given
            LocalDateTime deletedAt = LocalDateTime.now();
            Article testArticle = Article.builder()
                .build();
            testArticle.delete(deletedAt);

            // when
            LocalDateTime newDeletedAt = LocalDateTime.now().plusSeconds(10);
            testArticle.delete(newDeletedAt);

            // then
            assertThat(testArticle.getDeletedAt()).isEqualTo(deletedAt);
            assertThat(testArticle.getDeletedAt()).isNotEqualTo(newDeletedAt);
            assertThat(testArticle.isDeleted()).isTrue();
        }
    }

    @DisplayName("restore()는")
    @Nested
    class Context_restore{

        @DisplayName("게시글을 복원할 수 있다.")
        @Test
        void article_deletedAt_willRemove(){
            // given
            LocalDateTime deletedAt = LocalDateTime.now();
            Article testArticle = Article.builder()
                .build();

            // when
            testArticle.delete(deletedAt);
            testArticle.restore();

            // then
            assertThat(testArticle.getDeletedAt()).isNull();
        }
    }
}
