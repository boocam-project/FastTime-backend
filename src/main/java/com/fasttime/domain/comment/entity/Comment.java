package com.fasttime.domain.comment.entity;

import com.fasttime.domain.article.entity.Article;
import com.fasttime.domain.comment.dto.response.CommentResponseDTO;
import com.fasttime.domain.member.entity.Member;
import com.fasttime.global.common.BaseTimeEntity;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Comment extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JoinColumn(name = "article_id")
    @ManyToOne
    private Article article;

    @JoinColumn(name = "member_Id")
    @ManyToOne
    private Member member;

    private String content;

    private boolean anonymity;

    @JoinColumn(name = "comment_parent_id")
    @ManyToOne
    private Comment parentComment;

    @OneToMany(mappedBy = "parentComment")
    private List<Comment> childComments = new ArrayList<>();

    @Builder
    public Comment(Long id, Article article, Member member, String content, boolean anonymity,
        Comment parentComment) {
        this.id = id;
        this.article = article;
        this.member = member;
        this.content = content;
        this.anonymity = anonymity;
        this.parentComment = parentComment;
    }

    @Override
    public void delete(LocalDateTime currentTime) {
        super.delete(currentTime);
        if (!this.childComments.isEmpty()) {
            this.childComments.forEach(comment -> {
                comment.delete(currentTime);
            });
        }
    }

    public void updateContent(String content) {
        this.content = content;
    }

    public CommentResponseDTO toCommentResponseDTO() {
        long parentCommentId = -1L;
        boolean isChildComment = this.parentComment != null;
        int deletedChildCommentCount = 0;
        if (isChildComment) {
            parentCommentId = this.parentComment.getId();
        }
        for (Comment comment : this.childComments) {
            if (comment.isDeleted()) {
                deletedChildCommentCount++;
            }
        }
        return CommentResponseDTO.builder().commentId(this.id).articleId(this.article.getId())
            .memberId(this.member.getId()).nickname(this.member.getNickname()).content(this.content)
            .anonymity(this.anonymity).parentCommentId(parentCommentId)
            .childCommentCount(this.childComments.size() - deletedChildCommentCount)
            .createdAt(dateTimeParse(this.getCreatedAt()))
            .updatedAt(dateTimeParse(this.getUpdatedAt()))
            .deletedAt(dateTimeParse(this.getDeletedAt())).build();
    }

    private String dateTimeParse(LocalDateTime dateTime) {
        return (dateTime != null) ? dateTime.format(
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) : null;
    }
}
