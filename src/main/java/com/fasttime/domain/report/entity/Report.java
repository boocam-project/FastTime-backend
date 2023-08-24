package com.fasttime.domain.report.entity;

import com.fasttime.domain.member.entity.Member;
import com.fasttime.domain.post.entity.Post;
import com.fasttime.domain.report.dto.ReportDTO;
import com.fasttime.global.common.BaseTimeEntity;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Report extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JoinColumn(name = "member_id")
    @ManyToOne
    private Member member;

    @JoinColumn(name = "post_id")
    @ManyToOne
    private Post post;

    @Builder
    public Report(Long id, Member member, Post post) {
        this.id = id;
        this.member = member;
        this.post = post;
    }

    public ReportDTO toDTO() {
        return ReportDTO.builder().id(this.id).memberId(this.member.getId())
            .postId(this.post.getId()).build();
    }
}
