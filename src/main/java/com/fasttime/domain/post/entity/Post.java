package com.fasttime.domain.post.entity;

import com.fasttime.domain.member.entity.Member;
import com.fasttime.global.common.BaseTimeEntity;
import javax.persistence.Embedded;
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

    private boolean anounumity;

    private int likeCount;

    private int hateCount;

    private Post(Member member, String title, String content, boolean anounumity,
        int likeCount, int hateCount) {
        this.member = member;
        this.title = title;
        this.content = new PostContent(content);
        this.anounumity = anounumity;
        this.likeCount = likeCount;
        this.hateCount = hateCount;
    }

    public static Post of(Member member, String title, String content, boolean anounumity) {
        return new Post(member, title, content, anounumity, 0, 0);
    }

    public void update(String content) {
        this.content.updateContent(content);
    }

    public String getContent() {
        return content.getContent();
    }
}
