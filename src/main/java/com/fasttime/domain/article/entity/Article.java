package com.fasttime.domain.article.entity;

import com.fasttime.domain.article.exception.ArticleDeletedException;
import com.fasttime.domain.article.exception.ArticleReportedException;
import com.fasttime.domain.article.exception.BadArticleReportStatusException;
import com.fasttime.domain.member.entity.Member;
import com.fasttime.global.common.BaseTimeEntity;
import jakarta.persistence.Column;
import jakarta.persistence.ConstraintMode;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@EqualsAndHashCode(of = "id", callSuper = false)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(indexes = {
    @Index(name = "idx_created_at", columnList = "createdAt")
})
@Entity
public class Article extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "member_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Member member;

    private String title;

    @Embedded
    private ArticleContent content;

    private boolean anonymity;

    private int likeCount;

    private int hateCount;

    private int commentCount;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "enum('NORMAL', 'WAIT_FOR_REPORT_REVIEW', 'REPORT_ACCEPT', 'REPORT_REJECT')")
    private ReportStatus reportStatus;

    @Builder
    private Article(Long id, Member member, String title, String content, boolean anonymity,
        int likeCount, int hateCount, int commentCount, ReportStatus reportStatus) {
        this.id = id;
        this.member = member;
        this.title = title;
        this.content = new ArticleContent(content);
        this.anonymity = anonymity;
        this.likeCount = likeCount;
        this.hateCount = hateCount;
        this.commentCount = commentCount;
        this.reportStatus = reportStatus;
    }

    public static Article createNewArticle(Member member, String title, String content,
        boolean anonymity) {
        return Article.builder()
            .member(member)
            .title(title)
            .content(content)
            .anonymity(anonymity)
            .likeCount(0)
            .hateCount(0)
            .commentCount(0)
            .reportStatus(ReportStatus.NORMAL)
            .build();
    }

    public void update(String title, String content) {
        if (reportStatus.equals(ReportStatus.WAIT_FOR_REPORT_REVIEW)) {
            throw new ArticleReportedException(String.format(
                "This article is reported. So cannot update this article. / requestArticleId = %d",
                this.id));
        }

        if (this.isDeleted()) {
            throw new ArticleDeletedException(String.format(
                "This article is deleted. So cannot update this article. / requestArticleId = %d",
                this.id));
        }

        this.title = title;
        this.content.updateContent(content);
    }

    public String getContent() {
        return content.getContent();
    }


    public void increaseCommentCount() {
        this.commentCount++;
    }

    public void decreaseCommentCount() {
        this.commentCount--;
    }

    @Override
    public void delete(LocalDateTime currentTime) {
        super.delete(currentTime);
    }

    public void likeOrHate(boolean isLike, boolean increase) {
        if (isLike) {
            this.likeCount += increase ? 1 : -1;
        } else {
            this.hateCount += increase ? 1 : -1;
        }
    }

    @Override
    public void restore() {
        super.restore();
    }

    public void transToWaitForReview() {
        if (this.reportStatus != ReportStatus.NORMAL) {
            throw new BadArticleReportStatusException();
        }

        this.reportStatus = ReportStatus.WAIT_FOR_REPORT_REVIEW;
    }

    public void approveReport(LocalDateTime currentTime) {
        if (this.reportStatus != ReportStatus.WAIT_FOR_REPORT_REVIEW) {
            throw new BadArticleReportStatusException();
        }

        this.reportStatus = ReportStatus.REPORT_ACCEPT;
        delete(currentTime);
    }

    public void rejectReport() {
        if (this.reportStatus != ReportStatus.WAIT_FOR_REPORT_REVIEW) {
            throw new BadArticleReportStatusException();
        }

        this.reportStatus = ReportStatus.REPORT_REJECT;
    }
}