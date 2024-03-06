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
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class Resume extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String content;
    @ColumnDefault("0")
    private int rating;

    @ManyToOne
    private Member writer;

    public static Resume createNewResume(Member member, String title, String content) {
        return Resume.builder()
                .title(title)
                .content(content)
                .writer(member)
                .build();
    }

    public void updateResume(String title, String content) {
        this.title = title;
        this.content = content;
    }

    public void softDelete() {
        delete(LocalDateTime.now());
    }
}
