package com.fasttime.domain.review.entity;

import com.fasttime.domain.bootcamp.entity.BootCamp;
import com.fasttime.domain.member.entity.Member;
import com.fasttime.global.common.BaseTimeEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Getter
public class Review extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private int rating;

    private String content;

    @Builder.Default
    @OneToMany(mappedBy = "review", cascade = CascadeType.ALL)
    private Set<ReviewTag> reviewTags = new HashSet<>();

    @JoinColumn(name = "member_id")
    @ManyToOne
    private Member member;

    @JoinColumn(name = "bootcamp_id")
    @ManyToOne
    private BootCamp bootCamp;

    public void setReviewTags(Set<ReviewTag> reviewTags) {
        this.reviewTags = reviewTags;
    }

    public void updateReviewDetails(String title, int rating, String content) {
        this.title = title;
        this.rating = rating;
        this.content = content;
    }

    public void softDelete() {
        delete(LocalDateTime.now());
    }
}
