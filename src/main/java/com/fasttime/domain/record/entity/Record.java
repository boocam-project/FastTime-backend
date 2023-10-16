package com.fasttime.domain.record.entity;

import com.fasttime.domain.member.entity.Member;
import com.fasttime.domain.post.entity.Post;
import com.fasttime.domain.record.dto.RecordDTO;
import com.fasttime.global.common.BaseTimeEntity;
import javax.persistence.Column;
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
import org.hibernate.annotations.Comment;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Record extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JoinColumn(name = "member_id")
    @ManyToOne
    private Member member;

    @JoinColumn(name = "post_id")
    @ManyToOne
    private Post post;

    @Comment("좋아요 = 1, 싫어요 = 0")
    @Column(name = "type")
    private boolean isLike;

    @Builder
    public Record(Long id, Member member, Post post, boolean isLike) {
        this.id = id;
        this.member = member;
        this.post = post;
        this.isLike = isLike;
    }

    public RecordDTO toDTO() {
        return RecordDTO.builder().id(this.id).postId(this.post.getId())
            .memberId(this.member.getId()).isLike(this.isLike).build();
    }
}
