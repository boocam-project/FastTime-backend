package com.fasttime.domain.resume.entity;

import com.fasttime.domain.member.entity.Member;
import com.fasttime.global.common.BaseTimeEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Resume extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private String content;

    @ColumnDefault("0")
    private int likeCount;

    @ColumnDefault("0")
    private int viewCount;

    @ManyToOne
    private Member writer;

    @Builder
    public Resume(Long id, String title, String content, int likeCount, int viewCount,
            Member writer) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.likeCount = likeCount;
        this.viewCount = viewCount;
        this.writer = writer;
    }

    public void updateResume(String title, String content) {
        this.title = title;
        this.content = content;
    }

    @Override
    public void delete(LocalDateTime currentTime) {
        super.delete(currentTime);
    }

    public void like() {
        this.likeCount += 1;
    }

    public void view() {
        this.viewCount += 1;
    }
}
