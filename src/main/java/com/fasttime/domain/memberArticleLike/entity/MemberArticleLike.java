package com.fasttime.domain.memberArticleLike.entity;

import com.fasttime.domain.article.entity.Article;
import com.fasttime.domain.member.entity.Member;
import com.fasttime.domain.memberArticleLike.dto.MemberArticleLikeDTO;
import com.fasttime.global.common.BaseTimeEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(
    name = "MEMBER_ARTICLE_LIKE",
    uniqueConstraints = @UniqueConstraint(columnNames = {"member_id", "article_id"}))
public class MemberArticleLike extends BaseTimeEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @JoinColumn(name = "member_id")
  @ManyToOne
  private Member member;

  @JoinColumn(name = "article_id")
  @ManyToOne
  private Article article;

  @Comment("좋아요 = 1, 싫어요 = 0")
  @Column(name = "type")
  private boolean isLike;

  @Builder
  public MemberArticleLike(Long id, Member member, Article article, boolean isLike) {
    this.id = id;
    this.member = member;
    this.article = article;
    this.isLike = isLike;
  }

  public MemberArticleLikeDTO toDTO() {
    return MemberArticleLikeDTO.builder()
        .id(this.id)
        .postId(this.article.getId())
        .memberId(this.member.getId())
        .isLike(this.isLike)
        .build();
  }
}
