package com.fasttime.domain.comment.entity;

import com.fasttime.domain.member.entity.Member;
import com.fasttime.domain.post.entity.Post;
import com.fasttime.global.common.BaseTimeEntity;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Comment extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JoinColumn(name = "post_id")
    @ManyToOne
    private Post post;

    @JoinColumn(name = "member_Id")
    @ManyToOne
    private Member member;

    private String content;

    private boolean anonymity;

    @JoinColumn(name = "comment_parent_id")
    @ManyToOne
    private Comment parentComment;
}
