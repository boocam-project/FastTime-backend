package com.fasttime.domain.post.entity;

import com.fasttime.domain.member.entity.Member;
import com.fasttime.domain.post.exception.PostDeletedException;
import com.fasttime.domain.post.exception.PostReportedException;
import com.fasttime.global.common.BaseTimeEntity;
import java.time.LocalDateTime;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
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
public class Post extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;

    private String title;

    @Embedded
    private PostContent content;

    private boolean anonymity;

    private int likeCount;

    private int hateCount;

    @Enumerated(EnumType.STRING)
    private ReportStatus reportStatus;

    @Builder
    private Post(Long id, Member member, String title, String content, boolean anonymity,
        int likeCount, int hateCount, ReportStatus reportStatus) {
        this.id = id;
        this.member = member;
        this.title = title;
        this.content = new PostContent(content);
        this.anonymity = anonymity;
        this.likeCount = likeCount;
        this.hateCount = hateCount;
        this.reportStatus = reportStatus;
    }

    public static Post createNewPost(Member member, String title, String content,
        boolean anonymity) {
        return Post.builder()
            .member(member)
            .title(title)
            .content(content)
            .anonymity(anonymity)
            .likeCount(0)
            .hateCount(0)
            .reportStatus(ReportStatus.NORMAL)
            .build();
    }

    public void update(String title, String content) {
        if (reportStatus.equals(ReportStatus.REPORTED)) {
            throw new PostReportedException(String.format(
                "This post is reported. So cannot update this post. / requestPostId = %d",
                this.id));
        }

        if (this.isDeleted()) {
            throw new PostDeletedException(String.format(
                "This post is deleted. So cannot update this post. / requestPostId = %d", this.id));
        }

        this.title = title;
        this.content.updateContent(content);
    }

    public String getContent() {
        return content.getContent();
    }

    @Override
    public void delete(LocalDateTime currentTime) {
        super.delete(currentTime);
    }

    public void likeOrHate(boolean isLike, boolean increase) {
        if (isLike) {
            if (increase) {
                this.likeCount++;
            } else {
                this.likeCount--;
            }
        } else {
            if (increase) {
                this.hateCount++;
            } else {
                this.hateCount--;
            }
        }
    }

    @Override
    public void restore() {
        super.restore();
    }

    public void report() {
        if (this.reportStatus == ReportStatus.NORMAL) {
            this.reportStatus = ReportStatus.REPORTED;
        }
    }

    public void approveReport(LocalDateTime currentTime) {
        if (this.reportStatus == ReportStatus.REPORTED) {
            delete(currentTime);
        }
    }

    public void rejectReport() {
        if (this.reportStatus == ReportStatus.REPORTED) {
            this.reportStatus = ReportStatus.REPORT_REJECTED;
        }
    }
}
